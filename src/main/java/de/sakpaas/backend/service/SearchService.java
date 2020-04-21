package de.sakpaas.backend.service;

import de.sakpaas.backend.model.CoordinateDetails;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.SearchResultObject;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

  private static final Logger LOGGER = LoggerFactory.getLogger(SearchService.class);
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
    CoordinateDetails nominatimResultLocationDtoList =
        searchMappingService.search(key);
    List<Location> locationList =
        locationService.findByCoordinates(nominatimResultLocationDtoList.getLatitude(),
            nominatimResultLocationDtoList.getLongitude());
    LOGGER.info(locationList.toString());

    return new SearchResultObject(nominatimResultLocationDtoList, locationList);
  }

}
