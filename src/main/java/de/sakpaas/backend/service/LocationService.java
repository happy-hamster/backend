package de.sakpaas.backend.service;

import com.google.common.util.concurrent.AtomicDouble;
import de.sakpaas.backend.dto.OSMResultLocationListDto;
import de.sakpaas.backend.model.Address;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.LocationDetails;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LocationService {
    private final LocationRepository locationRepository;
    private final LocationDetailsService locationDetailsService;
    private final AddressService addressService;
    private final MeterRegistry meterRegistry;
    private final LocationApiSearchDAS locationApiSearchDAS;
    private Logger LOGGER = LoggerFactory.getLogger(LocationService.class);
    private Counter importLocationInsertCounter;
    private Counter importLocationUpdateCounter;
    private Counter importLocationDeleteCounter;
    private Gauge importLocationGauge;
    private Gauge deleteLocationGauge;
    private AtomicDouble importLocationProgress;
    private AtomicDouble deleteLocationProgress;


    @Autowired
    public LocationService(LocationRepository locationRepository,
                           LocationDetailsService locationDetailsService,
                           AddressService addressService,
                           MeterRegistry meterRegistry, LocationApiSearchDAS locationApiSearchDAS) {
        this.locationRepository = locationRepository;
        this.locationDetailsService = locationDetailsService;
        this.addressService = addressService;
        this.meterRegistry = meterRegistry;
        this.importLocationProgress = new AtomicDouble();
        this.deleteLocationProgress = new AtomicDouble();

        importLocationInsertCounter = Counter
                .builder("import")
                .description("Total number of OSM locations imported and newly inserted")
                .tags("type", "location", "action", "insert")
                .register(meterRegistry);
        importLocationUpdateCounter = Counter
                .builder("import")
                .description("Total number of OSM locations imported and updated")
                .tags("type", "location", "action", "update")
                .register(meterRegistry);
        importLocationDeleteCounter = Counter
                .builder("import")
                .description("Total number of OSM locations imported and updated")
                .tags("type", "location", "action", "delete")
                .register(meterRegistry);
        Gauge.builder("import_progress", () -> this.importLocationProgress.get())
                .description("Percentage of OSM locations imported (0.0 to 1.0)")
                .tags("version", "v2")
                .register(meterRegistry);
        Gauge.builder("delete_progress", () -> this.deleteLocationProgress.get())
                .description("Percentage of OSM locations deleted (0.0 to 1.0)")
                .tags("version", "v2")
                .register(meterRegistry);
        this.locationApiSearchDAS = locationApiSearchDAS;
    }

    public static double distanceInKm(double lat1, double lon1, double lat2, double lon2) {
        // https://www.daniel-braun.com/technik/distanz-zwischen-zwei-gps-koordinaten-in-java-berchenen/
        int radius = 6371;
        double lat = Math.toRadians(lat2 - lat1);
        double lon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(lat / 2) * Math.sin(lat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(lon / 2) * Math.sin(lon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = radius * c;
        return Math.abs(d);
    }

    public Optional<Location> getById(long id) {
        return locationRepository.findById(id);
    }

    public List<Location> findByCoordinates(Double lat, Double lon) {
        List<Location> list = locationRepository.findByLatitudeBetweenAndLongitudeBetween(lat - 0.1, lat + 0.1, lon - 0.1, lon + 0.1);
        return list.stream()
                .sorted(Comparator.comparingDouble(l -> distanceInKm(l.getLatitude(), l.getLongitude(), lat, lon)))
                .limit(100)
                .collect(Collectors.toList());
    }

    public Location save(Location location) {
        return locationRepository.save(location);
    }


    /***
     * Making an Request to the OverpassAPI, insert or update the Locations in the Database, deleting the unused locations
     */
    public void updateDatabase() {
        // Reset import progress
        importLocationProgress.set(0.0);
        deleteLocationProgress.set(0.0);

        // Download data from OSM
        LOGGER.warn("Starting OSM import... (1/4)");
        List<OSMResultLocationListDto.OMSResultLocationDto> results = locationApiSearchDAS.getLocationsForCountry("DE");
        LOGGER.info("Finished receiving data from OSM! (1/4)");
        // Checking if API Call has a legit result
        if (results.size() < 1000) {
            throw new IllegalStateException("API returns to few results! This doesn't seem right...");
        }

        // Getting IDs stored in the Database right now
        List<Long> locationIds = locationRepository.getAllIds();
        LOGGER.info("Pre Update Location Count: " + locationIds.size());
        // Sort data by id before import, inserts should be faster for sorted ids
        LOGGER.warn("Sorting OSM data... (2/4)");
        results.sort(Comparator.comparingLong(OSMResultLocationListDto.OMSResultLocationDto::getId));
        LOGGER.info("Finished sorting OSM data! (2/4)");

        // Insert or update data one by one in the table
        LOGGER.warn("Importing OSM data to database... (3/4)");
        for (int i = 0; i < results.size(); i++) {
            OSMResultLocationListDto.OMSResultLocationDto osmLocation = results.get(i);
            if (locationIds.contains(osmLocation.getId())) {
                // Updating an existing Location
                updateLocation(osmLocation);
                importLocationUpdateCounter.increment();
                // Removing still existing database Ids from List
                locationIds.remove(results.get(i).getId());
            } else {
                // Creating a Database Entry for a new Location
                createNewLocation(osmLocation);
                importLocationInsertCounter.increment();
            }

            // Report
            double progress = ((double) i) / results.size();
            importLocationProgress.set(progress);
            if (i % 100 == 0) {
                LOGGER.info("OSM Import: " + progress * 100.0 + " %");
            }
        }
        importLocationProgress.set(1.0);

        LOGGER.warn("Delete not existing Locations... (3/4)");
        for (int i = 0; i < locationIds.size(); i++) {
            locationRepository.deleteById(locationIds.get(i));
            importLocationDeleteCounter.increment();

            double progress = ((double) i) / locationIds.size();
            deleteLocationProgress.set(progress);
            if (i % 100 == 0) {
                LOGGER.info("Location deletion: " + progress * 100.0 + " %");
            }
        }
        deleteLocationProgress.set(1.0);
        LOGGER.info("Finished deleting " + locationIds.size() + " not existing Locations! (3/4)");

        LOGGER.info("Finished data import from OSM! (4/4)");
    }


    /**
     * Updating an existing Database Entry
     *
     * @param osmLocation New Location
     */
    private void updateLocation(OSMResultLocationListDto.OMSResultLocationDto osmLocation) {
        Optional<Location> optionalLocation = locationRepository.findById(osmLocation.getId());
        if (optionalLocation.isPresent()) {
            Location location = optionalLocation.get();
            LocationDetails details = location.getDetails();
            details.setType(osmLocation.getType());
            details.setOpeningHours(osmLocation.getOpeningHours());
            details.setBrand(osmLocation.getBrand());
            locationDetailsService.save(details);

            locationDetailsService.save(details);
            Address address = location.getAddress();
            address.setCountry(osmLocation.getCountry());
            address.setCity(osmLocation.getCity());
            address.setPostcode(osmLocation.getPostcode());
            address.setStreet(osmLocation.getStreet());
            address.setHousenumber(osmLocation.getHousenumber());
            addressService.save(address);

            location.setName(osmLocation.getName() != null ? osmLocation.getName() : "Supermarkt");
            location.setLatitude(osmLocation.getCoordinates().getLat());
            location.setLongitude(osmLocation.getCoordinates().getLon());
            this.save(location);
        } else {
            LOGGER.error("Unable to find Location with ID=" + osmLocation.getId() + ". Maybe removed during DatabaseUpdate?");
        }
    }

    /**
     * Creating a new Location Entry in the Database
     *
     * @param osmLocation Location that will be added to the Database
     */
    private void createNewLocation(OSMResultLocationListDto.OMSResultLocationDto osmLocation) {
        LocationDetails details = new LocationDetails(
                osmLocation.getType(),
                osmLocation.getOpeningHours(),
                osmLocation.getBrand()
        );
        locationDetailsService.save(details);

        Address address = new Address(
                osmLocation.getCountry(),
                osmLocation.getCity(),
                osmLocation.getPostcode(),
                osmLocation.getStreet(),
                osmLocation.getHousenumber()
        );
        addressService.save(address);

        Location location = new Location(
                osmLocation.getId(),
                osmLocation.getName() != null ? osmLocation.getName() : "Supermarkt",
                osmLocation.getCoordinates().getLat(),
                osmLocation.getCoordinates().getLon(),
                details,
                address
        );
        this.save(location);
    }
}
