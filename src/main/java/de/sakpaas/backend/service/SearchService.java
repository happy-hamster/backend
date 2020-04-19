package de.sakpaas.backend.service;

import de.sakpaas.backend.model.Location;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

  private final LocationService locationService;
  private final SearchMappingService searchMappingService;

  /**
   * Searches for a specific key, calculates the central point as coordinates and returns
   * additionally a list of locations around the coordinates.
   *
   * @param locationService      The service for getting locations based on specific coordinates
   * @param searchMappingService The service for actually making the REST request
   */
  @Autowired
  public SearchService(LocationService locationService,
      SearchMappingService searchMappingService) {
    this.locationService = locationService;
    this.searchMappingService = searchMappingService;
  }

  /**
   * Handles the query key. Delegates the REST request and gets the locations based on the central
   * coordinates of the search result.
   *
   * @param key The query key
   * @return An object with the central coordinates and a list of locations
   */
  public SearchResultObject search(String key) {
    Map<String, Double> nominatimResultLocationDtoList =
        searchMappingService.search(key);
    List<Location> locationList =
        locationService.findByCoordinates(nominatimResultLocationDtoList.get("lat"),
            nominatimResultLocationDtoList.get("lon"));

    return new SearchResultObject(nominatimResultLocationDtoList, locationList);
  }

  @AllArgsConstructor
  @Getter
  public static class SearchResultObject {

    private final Map<String, Double> coordinates;
    private final List<Location> locationList;
  }
}
