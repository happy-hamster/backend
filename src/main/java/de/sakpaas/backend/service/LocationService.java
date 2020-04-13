package de.sakpaas.backend.service;

import de.sakpaas.backend.dto.NominatimSearchResultListDto;
import de.sakpaas.backend.dto.NominatimSearchResultListDto.NominatimResultLocationDto;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.util.CoordinatesUtils;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LocationService {

  private final LocationRepository locationRepository;
  private final OpenStreetMapService openStreetMapService;

  @Value("${app.search-api-url}")
  private String searchApiUrl;

  /**
   * Default Constructor. Handles the Dependency Injection and Meter Initialisation and Registering
   *
   * @param locationRepository   The Location Repository
   * @param openStreetMapService The OpenStreetMap Service
   */
  @Autowired
  public LocationService(LocationRepository locationRepository,
      OpenStreetMapService openStreetMapService) {
    this.locationRepository = locationRepository;
    this.openStreetMapService = openStreetMapService;
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
            .comparingDouble(
                l -> CoordinatesUtils.distanceInKm(l.getLatitude(), l.getLongitude(), lat, lon)))
        .limit(100)
        .collect(Collectors.toList());
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

  /**
   * Makes a Request to the OverpassAPI, inserts or updates the Locations in the Database, deletes
   * the unused locations.
   */
  public void updateDatabase() {
    openStreetMapService.updateDatabase();
  }

  /**
   * Saves a Location to the Database.
   *
   * @param location Location that will be saved
   */
  public void save(Location location) {
    locationRepository.save(location);
  }
}
