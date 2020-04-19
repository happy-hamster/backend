package de.sakpaas.backend.v2.controller;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import de.sakpaas.backend.BackendApplication;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.Occupancy;
import de.sakpaas.backend.service.LocationService;
import de.sakpaas.backend.service.OccupancyService;
import de.sakpaas.backend.service.OpenStreetMapService;
import de.sakpaas.backend.service.PresenceService;
import de.sakpaas.backend.service.SearchService;
import de.sakpaas.backend.service.SearchService.SearchResultObject;
import de.sakpaas.backend.v2.dto.LocationResultLocationDto;
import de.sakpaas.backend.v2.dto.OccupancyReportDto;
import de.sakpaas.backend.v2.dto.SearchResultDto;
import de.sakpaas.backend.v2.mapper.LocationMapper;
import de.sakpaas.backend.v2.mapper.SearchResultMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RequestMapping("/v2/locations")
@RestController
public class LocationController {

  private static final String MAPPING_POST_OCCUPANCY = "/{locationId}/occupancy";
  private static final String MAPPING_POST_CHECKIN = "/{locationId}/check-in";
  private static final String MAPPING_BY_ID = "/{locationId}";
  private static final String MAPPING_START_DATABASE = "/generate/{key}";
  private static final String MAPPING_SEARCH_LOCATION = "/search/{key}";
  private final LocationService locationService;
  private final SearchService searchService;
  private final OpenStreetMapService openStreetMapService;
  private final LocationMapper locationMapper;
  private final SearchResultMapper searchResultMapper;
  private final OccupancyService occupancyService;
  private final PresenceService presenceService;
  private final AtomicBoolean importState;


  /**
   * Constructor that injects the needed dependencies.
   *
   * @param locationService      The Location Service //   * @param searchService        The Service
   *                             for searching the database
   * @param openStreetMapService The OpenStreetMap Service
   * @param locationMapper       An OSM Location to Location Mapper
   * @param searchResultMapper   TODO
   * @param occupancyService     The Occupancy Service
   * @param presenceService      The Presence Service
   */
  public LocationController(LocationService locationService,
      SearchService searchService,
      OpenStreetMapService openStreetMapService,
      LocationMapper locationMapper,
      SearchResultMapper searchResultMapper,
      OccupancyService occupancyService,
      PresenceService presenceService) {
    this.locationService = locationService;
    this.searchService = searchService;
    this.openStreetMapService = openStreetMapService;
    this.locationMapper = locationMapper;
    this.searchResultMapper = searchResultMapper;
    this.occupancyService = occupancyService;
    this.presenceService = presenceService;
    this.importState = new AtomicBoolean(false);
  }

  /**
   * Get Endpoint to receive all Locations around a given location.
   *
   * @param latitude  Latitude of the Location.
   * @param longitude Longitude of the Location.
   * @return List of all Locations in the Area.
   */
  @GetMapping
  @ResponseBody
  public ResponseEntity<List<LocationResultLocationDto>> getLocation(@RequestParam Double latitude,
      @RequestParam
          Double longitude) {
    List<Location> searchResult = locationService.findByCoordinates(latitude, longitude);

    if (searchResult.isEmpty()) {
      return new ResponseEntity<>(new ArrayList<>(), OK);
    }

    List<LocationResultLocationDto> response = searchResult.stream()
        .map(locationMapper::mapToOutputDto)
        .collect(toList());

    return new ResponseEntity<>(response, OK);
  }

  /**
   * Get Endpoint to request a specific Location.
   *
   * @param locationId Id of the specific Location
   * @return The Location Object
   */
  @GetMapping(value = MAPPING_BY_ID)
  public ResponseEntity<LocationResultLocationDto> getById(
      @PathVariable("locationId") Long locationId) {
    Location location = locationService.getById(locationId).orElse(null);

    if (location == null) {
      return ResponseEntity.notFound().build();
    }

    return new ResponseEntity<>(locationMapper.mapToOutputDto(location), OK);
  }

  /**
   * Post Endpoint to create a new Occupancy Report.
   *
   * @param occupancyReportDto OccupancyReportDto send by the Client
   * @param locationId         LocationId of the Location the Report is for
   * @return Returns if the Report was created successfully
   */
  @PostMapping(value = MAPPING_POST_OCCUPANCY)
  public ResponseEntity<LocationResultLocationDto> postNewOccupancy(
      @Valid @RequestBody OccupancyReportDto occupancyReportDto,
      @PathVariable("locationId") Long locationId) {
    occupancyReportDto.setLocationId(locationId);
    Location location = locationService.getById(locationId).orElse(null);

    if (location == null) {
      return ResponseEntity.notFound().build();
    }

    occupancyService.save(new Occupancy(location, occupancyReportDto.getOccupancy(),
        occupancyReportDto.getClientType()));

    return new ResponseEntity<>(locationMapper.mapToOutputDto(location), CREATED);
  }

  /**
   * Post Endpoint to create a new CheckIn Event.
   *
   * @param locationId LocationId of the Location the CheckIn is for
   * @return Returns if the Creation was successful
   */
  @PostMapping(value = MAPPING_POST_CHECKIN)
  public ResponseEntity<String> postNewCheckIn(@PathVariable("locationId") Long locationId) {
    Location location = locationService.getById(locationId).orElse(null);

    if (location != null) {
      presenceService.addNewCheckin(location);
      return ResponseEntity.status(CREATED).build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Get Endpoint to initiate the Database Update.
   *
   * @param key Secret key to authorize the update. Printed do the Log on startup
   * @return Returns if the Import was successful
   */
  @GetMapping(value = MAPPING_START_DATABASE)
  public ResponseEntity<String> startDatabase(@PathVariable("key") String key) {
    // Check key
    if (!key.equals(BackendApplication.GENERATED)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Permission denied");
    }

    // Check if it is the only query running
    if (importState.get()) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body("Already running");
    }

    // Lock database import
    importState.set(true);
    // Making the Database import
    openStreetMapService.updateDatabase();
    // Unlock database import
    importState.set(false);

    return ResponseEntity.ok("Success");
  }

  /**
   * Get Endpoint for searching.
   *
   * @param key The query Key
   * @return Returns if the coordinates and the locations list was correctly calculated
   */
  @GetMapping(value = MAPPING_SEARCH_LOCATION)
  public ResponseEntity<SearchResultDto> searchForLocations(
      @PathVariable("key") String key) {
    final SearchResultObject resultObject = searchService.search(key);
    return new ResponseEntity<>(searchResultMapper.mapToOutputDto(resultObject), OK);
  }
}
