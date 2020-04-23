package de.sakpaas.backend.service;

import com.google.common.annotations.VisibleForTesting;
import de.sakpaas.backend.dto.NominatimSearchResultListDto;
import de.sakpaas.backend.dto.NominatimSearchResultListDto.NominatimResultLocationDto;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.util.CoordinatesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LocationService {

  private final LocationRepository locationRepository;
  private final PresenceRepository presenceRepository;
  private final OccupancyRepository occupancyRepository;

  @Value("${app.search-api-url}")
  private String searchApiUrl;

  /**
   * Default Constructor. Handles the Dependency Injection and Meter Initialisation and Registering
   *
   * @param locationRepository The Location Repository
   */
  @Autowired
  public LocationService(LocationRepository locationRepository,
                         PresenceRepository presenceRepository,
                         OccupancyRepository occupancyRepository) {
    this.locationRepository = locationRepository;
    this.presenceRepository = presenceRepository;
    this.occupancyRepository = occupancyRepository;
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
     * Gets all Locations from a specific coordinate.
     *
     * @param lat  Latitude of the Location.
     * @param lon  Longitude of the Location.
     * @param type types of Locations
     * @return List of max 100 Locations around the given coordinates.
     */
    public List<Location> findByCoordinates(Double lat, Double lon, List<String> type) {
        List<Location> list = locationRepository
                .findByLatitudeBetweenAndLongitudeBetweenAndDetails_Type(lat - 0.1, lat + 0.1, lon - 0.1, lon + 0.1, type);
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
    HttpHeaders httpHeaders = new HttpHeaders();
    // Nominatim kommt wohl nicht auf "application/json" klar.
    httpHeaders.set(HttpHeaders.ACCEPT, "text/html");
    HttpEntity<String> entityReq = new HttpEntity<String>(httpHeaders);
    ResponseEntity<NominatimSearchResultListDto> response =
        restTemplate.exchange(url, HttpMethod.GET, entityReq, NominatimSearchResultListDto.class);


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
   * Saves a Location to the Database.
   *
   * @param location Location that will be saved
   */
  public Location save(Location location) {
    return locationRepository.save(location);
  }

  /**
   * Deletes the given Location and all depending entities.
   *
   * @param location Location that needs to be deleted
   */
  @VisibleForTesting
  protected void delete(Location location) {
    occupancyRepository.findByLocation(location).forEach(occupancyRepository::delete);
    presenceRepository.findByLocation(location).forEach(presenceRepository::delete);
    locationRepository.delete(location);
  }
}
