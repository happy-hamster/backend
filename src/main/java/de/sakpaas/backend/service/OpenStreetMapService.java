package de.sakpaas.backend.service;

import com.google.common.util.concurrent.AtomicDouble;
import de.sakpaas.backend.dto.OsmResultLocationListDto;
import de.sakpaas.backend.model.Address;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.LocationDetails;
import de.sakpaas.backend.util.ImportConfiguration;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class OpenStreetMapService {

  private static final Logger LOGGER = LoggerFactory.getLogger(LocationService.class);
  private final AtomicDouble importLocationProgress;
  private final AtomicDouble deleteLocationProgress;
  private final LocationApiSearchDas locationApiSearchDas;
  private final LocationRepository locationRepository;
  private final LocationDetailsService locationDetailsService;
  private final AddressService addressService;
  private final MeterRegistry meterRegistry;
  private final LocationService locationService;
  private Counter importLocationInsertCounter;
  private Counter importLocationUpdateCounter;
  private Counter importLocationDeleteCounter;

  private ImportConfiguration shoptypeListConfig;

  /**
   * Handles the OpenStreetMap database import and update.
   *
   * @param locationRepository     The Location Repository
   * @param locationDetailsService The Location Details Service
   * @param addressService         The Address Service
   * @param meterRegistry          The Meter Registry
   * @param locationApiSearchDas   The LocationApiSearchDas
   * @param locationService        The Location Service
   */
  @Autowired
  public OpenStreetMapService(LocationRepository locationRepository,
                              LocationDetailsService locationDetailsService,
                              AddressService addressService,
                              MeterRegistry meterRegistry,
                              LocationApiSearchDas locationApiSearchDas,
                              LocationService locationService,
                              ImportConfiguration shoptypeListConfig) {
    this.locationRepository = locationRepository;
    this.locationDetailsService = locationDetailsService;
    this.addressService = addressService;
    this.locationApiSearchDas = locationApiSearchDas;
    this.meterRegistry = meterRegistry;
    this.locationService = locationService;
    this.shoptypeListConfig = shoptypeListConfig;

    this.importLocationProgress = new AtomicDouble();
    this.deleteLocationProgress = new AtomicDouble();
    initiateCounter();
    initiateGauge();
  }

  /**
   * Initiates the counter variables for counting the database entries during import, update and
   * deletion.
   */
  private void initiateCounter() {
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
  }

  /**
   * Builds the Gauge for database monitoring.
   */
  private void initiateGauge() {
    Gauge.builder("import_progress", this.importLocationProgress::get)
        .description("Percentage of OSM locations imported (0.0 to 1.0)")
        .tags("version", "v2")
        .register(meterRegistry);
    Gauge.builder("delete_progress", this.deleteLocationProgress::get)
        .description("Percentage of OSM locations deleted (0.0 to 1.0)")
        .tags("version", "v2")
        .register(meterRegistry);
  }

  /**
   * Makes a Request to the OverpassAPI, inserts or updates the Locations in the Database, deletes
   * the unused locations.
   */
  @Transactional
  public void updateDatabase() {
    // Reset import progress
    importLocationProgress.set(0.0);
    deleteLocationProgress.set(0.0);

    // Download data from OSM
    LOGGER.warn("Starting OSM import... (1/4)");
    List<OsmResultLocationListDto.OsmResultLocationDto> results =
        locationApiSearchDas.getLocationsForCountry(shoptypeListConfig);
    LOGGER.info("Finished receiving data from OSM! (1/4)");
    LOGGER.info("received ({}) Locations for Country: ({}) from OSM", results.size(),
        shoptypeListConfig.getCountry());
    // Checking if API Call has a legit result
    if (results.size() < 10) {
      throw new IllegalStateException(
          "API returns too few results! This doesn't seem right...");
    }

    // Getting IDs stored in the Database right now
    List<Long> locationIds =
        locationRepository.getAllIdsForCountry(shoptypeListConfig.getCountry());
    LOGGER.info("Pre Update Location Count: " + locationIds.size());
    // Sort data by id before import, inserts should be faster for sorted ids
    LOGGER.warn("Sorting OSM data... (2/4)");
    results.sort(Comparator.comparingLong(OsmResultLocationListDto.OsmResultLocationDto::getId));
    LOGGER.info("Finished sorting OSM data! (2/4)");

    // Insert or update data one by one in the table
    LOGGER.warn("Importing OSM data to database... (3/4)");
    for (int i = 0; i < results.size(); i++) {
      OsmResultLocationListDto.OsmResultLocationDto osmLocation = results.get(i);
      osmLocation.setCountry(shoptypeListConfig.getCountry());
      if (locationIds.contains(osmLocation.getId())) {
        // Updating an existing Location
        updateLocation(osmLocation);
        importLocationUpdateCounter.increment();
        // Removing still existing database Ids from List
        locationIds.remove(osmLocation.getId());
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
      try {
        locationRepository
            .findById(locationIds.get(i))
            .ifPresent(locationService::delete);
        importLocationDeleteCounter.increment();
      } catch (Exception e) {
        LOGGER.warn("An unknown error occurred while deleting Location with Id ({})",
            locationIds.get(i), e);
      }

      double progress = ((double) i) / locationIds.size();
      deleteLocationProgress.set(progress);
      if (i % 100 == 0) {
        LOGGER.info("Location deletion: " + progress * 100.0 + " %");
      }
    }
    deleteLocationProgress.set(1.0);
    LOGGER.info("Finished deleting " + locationIds.size() + " not existing Locations! (3/4)");

    LOGGER.info("Finished data import from OSM! (4/4)");
    int locationCount =
        locationRepository.getAllIdsForCountry(shoptypeListConfig.getCountry()).size();
    LOGGER.info("After Update Location Count: ({})", locationCount);
  }


  /**
   * Updating an existing Database Entry.
   *
   * @param osmLocation New Location
   */
  @Async
  public void updateLocation(OsmResultLocationListDto.OsmResultLocationDto osmLocation) {
    Optional<Location> optionalLocation = locationRepository.findById(osmLocation.getId());
    if (optionalLocation.isPresent()) {
      Location location = optionalLocation.get();
      LocationDetails details = location.getDetails();
      details.setType(osmLocation.getType());
      details.setOpeningHours(osmLocation.getOpeningHours());
      details.setBrand(osmLocation.getBrand());
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
      locationService.save(location);
    } else {
      LOGGER.error("Unable to find Location with ID=" + osmLocation.getId()
          + ". Maybe removed during DatabaseUpdate?");
    }
  }

  /**
   * Creating a new Location Entry in the Database.
   *
   * @param osmLocation Location that will be added to the Database
   */
  @Async
  public void createNewLocation(OsmResultLocationListDto.OsmResultLocationDto osmLocation) {
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
    locationService.save(location);
  }
}
