package de.sakpaas.backend.service;

import com.google.common.util.concurrent.AtomicDouble;
import de.sakpaas.backend.dto.NominatimSearchResultListDto;
import de.sakpaas.backend.dto.NominatimSearchResultListDto.NominatimResultLocationDto;
import de.sakpaas.backend.dto.OsmResultLocationListDto;
import de.sakpaas.backend.model.Address;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.LocationDetails;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LocationService {

  private static Logger LOGGER = LoggerFactory.getLogger(LocationService.class);
  private final LocationRepository locationRepository;
  private final LocationDetailsService locationDetailsService;
  private final AddressService addressService;
  private final LocationApiSearchDas locationApiSearchDas;
  private Counter importLocationInsertCounter;
  private Counter importLocationUpdateCounter;
  private Counter importLocationDeleteCounter;
  private AtomicDouble importLocationProgress;
  private AtomicDouble deleteLocationProgress;


  @Value("${app.search-api-url}")
  private String searchApiUrl;

  /**
   * Default Constructor. Handles the Dependency Injection and Meter Initialisation and Registering
   *
   * @param locationRepository     The Location Repository
   * @param locationDetailsService The Location Details Service
   * @param addressService         The Address Service
   * @param meterRegistry          The Meter Registry
   * @param locationApiSearchDas   The LocationApiSearchDas
   */
  @Autowired
  public LocationService(LocationRepository locationRepository,
                         LocationDetailsService locationDetailsService,
                         AddressService addressService,
                         MeterRegistry meterRegistry, LocationApiSearchDas locationApiSearchDas) {
    this.locationRepository = locationRepository;
    this.locationDetailsService = locationDetailsService;
    this.addressService = addressService;
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
    this.locationApiSearchDas = locationApiSearchDas;
  }

  /**
   * Calculates the distance between two given coordinates.
   *
   * @param lat1 Latitude of the first coordinate
   * @param lon1 Longitude of the first coordinate
   * @param lat2 Latitude of the second coordinate
   * @param lon2 Longitude of the second coordinate
   * @return Distance between the two coordinates in kilometres
   */
  public static double distanceInKm(double lat1, double lon1, double lat2, double lon2) {
    // https://www.daniel-braun.com/technik/distanz-zwischen-zwei-gps-koordinaten-in-java-berchenen/
    int radius = 6371;
    double lat = Math.toRadians(lat2 - lat1);
    double lon = Math.toRadians(lon2 - lon1);
    double a = Math.sin(lat / 2) * Math.sin(lat / 2)
        + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
        * Math.sin(lon / 2) * Math.sin(lon / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    double d = radius * c;
    return Math.abs(d);
  }

  /**
   * Gets a Location by its ID from the Database.
   *
   * @param id Id of the requested location
   * @return Location from the Database
   */
  public Optional<Location> getById(long id) {
    return locationRepository.findById(id);
  }

  /**
   * Gets all Locations from a specific coordinate.
   *
   * @param lat Latitude of the Location.
   * @param lon Longitude of the Location.
   * @return List of max 100 Locations around the given coordinates.
   */
  public List<Location> findByCoordinates(Double lat, Double lon) {
    List<Location> list = locationRepository
        .findByLatitudeBetweenAndLongitudeBetween(lat - 0.1, lat + 0.1, lon - 0.1, lon + 0.1);
    return list.stream()
        .sorted(Comparator
            .comparingDouble(l -> distanceInKm(l.getLatitude(), l.getLongitude(), lat, lon)))
        .limit(100)
        .collect(Collectors.toList());
  }

  /**
   * Saves a Location to the Database.
   *
   * @param location Location that will be saved
   * @return The inserted Location Object
   */
  public Location save(Location location) {
    return locationRepository.save(location);
  }


  /**
   * Makes a Request to the OverpassAPI, inserts or updates the Locations in the Database, deletes
   * the unused locations.
   */
  public void updateDatabase() {
    // Reset import progress
    importLocationProgress.set(0.0);
    deleteLocationProgress.set(0.0);

    // Download data from OSM
    LOGGER.warn("Starting OSM import... (1/4)");
    List<OsmResultLocationListDto.OsmResultLocationDto> results =
        locationApiSearchDas.getLocationsForCountry("DE");
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
    results.sort(Comparator.comparingLong(OsmResultLocationListDto.OsmResultLocationDto::getId));
    LOGGER.info("Finished sorting OSM data! (2/4)");

    // Insert or update data one by one in the table
    LOGGER.warn("Importing OSM data to database... (3/4)");
    for (int i = 0; i < results.size(); i++) {
      OsmResultLocationListDto.OsmResultLocationDto osmLocation = results.get(i);
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

      // Report importing progress
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
   * Updates an existing Database Entry.
   *
   * @param osmLocation New Location
   */
  private void updateLocation(OsmResultLocationListDto.OsmResultLocationDto osmLocation) {
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
      LOGGER.error("Unable to find Location with ID=" + osmLocation.getId()
          + ". Maybe removed during DatabaseUpdate?");
    }
  }

  /**
   * Creates a new Location Entry in the Database.
   *
   * @param osmLocation Location that will be added to the Database
   */
  private void createNewLocation(OsmResultLocationListDto.OsmResultLocationDto osmLocation) {
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

  /**
   * Searches in the Nominatim Microservice for the given key.
   *
   * @param key The search parameter. Multiple words are separated with %20.
   * @return The list of Locations in our database
   */
  public List<Location> search(String key) {
    // Makes a request to the Nominatim Microservice
    final String url = this.searchApiUrl + "/search/" + key + "?format=json";
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<NominatimSearchResultListDto> response =
        restTemplate.getForEntity(url, NominatimSearchResultListDto.class);


    if (response.getBody() == null) {
      return Collections.emptyList();
    }

    List<NominatimResultLocationDto> list = response.getBody().getElements();

    // Check if the ID is valid (is in database)
    return list.stream()
        .map(element -> getById(element.getOsmId()).orElse(null))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }
}
