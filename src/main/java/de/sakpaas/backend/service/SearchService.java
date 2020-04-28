package de.sakpaas.backend.service;

import de.sakpaas.backend.exception.EmptySearchQueryException;
import de.sakpaas.backend.model.CoordinateDetails;
import de.sakpaas.backend.model.SearchRequest;
import de.sakpaas.backend.model.SearchResultObject;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

  private static final Logger LOGGER = LoggerFactory.getLogger(SearchService.class);
  private final LocationService locationService;
  private final SearchMappingService searchMappingService;
  @Setter
  private static Set<String> knownBrands;
  private final LocationDetailsRepository locationDetailsRepository;

  /**
   * Searches for a specific key, calculates the central point as coordinates and returns
   * additionally a list of locations around the coordinates.
   *
   * @param locationService           The service for getting locations based on specific
   *                                  coordinates
   * @param searchMappingService      The service for actually making the REST request
   * @param locationDetailsRepository The Location Details Repository
   */
  @Autowired
  public SearchService(LocationService locationService,
                       SearchMappingService searchMappingService,
                       LocationDetailsRepository locationDetailsRepository) {
    this.locationService = locationService;
    this.searchMappingService = searchMappingService;
    this.locationDetailsRepository = locationDetailsRepository;
  }


  /**
   * Extracts all Brands that exists in the Database and saves them to the knownBrands List.
   * Also makes all brands lower case.
   */
  public void updateBrands() {
    knownBrands = locationDetailsRepository.getAllBrandNamesLower();
  }


  /**
   * Main search Method. Handles all the search logic
   *
   * @param query             Search Query
   * @param coordinateDetails Coordinates to assist the search
   * @return Result Locations
   */
  public SearchResultObject search(String query, CoordinateDetails coordinateDetails) {
    return new SearchResultObject(coordinateDetails, new ArrayList<>());
  }


  /**
   * Creates a new SearchRequest Object. Also makes all query entrys lower case.
   *
   * @param query             The SearchQuery
   * @param coordinateDetails The SearchCoordinates
   * @return A new SearchRequest
   */
  protected SearchRequest createRequest(String query, CoordinateDetails coordinateDetails)
      throws EmptySearchQueryException {
    return new SearchRequest();
  }

  /**
   * Possible Brand Names will be extracted from the Query and saved brands List.
   *
   * @param request The Request Object
   * @return the updated Request Object
   * @throws EmptySearchQueryException Will be thrown if the Query is
   *                                   Empty(needs to be implemented)
   */
  protected SearchRequest checkForBrands(SearchRequest request) {
    request.setBrands(
        request.getQuery().stream().filter(queryElement -> knownBrands.contains(queryElement))
            .collect(
                Collectors.toSet()));
    request.getQuery().removeAll(request.getBrands());
    return request;
  }


  /**
   * Creates a Nominatim Request and executes it. It updates the Coordinates of the Request.
   *
   * @param request The Request Object
   * @return the updated Request Object
   */
  protected SearchRequest getCoordinatesFromNominatim(SearchRequest request) {
    return request;
  }


  /**
   * Searches the Database for Locations that match at least one Brand of the Brand list in the.
   * Name or the Brand-Field.
   *
   * @param request The Request Object
   * @return the updated Request Object
   */
  protected SearchRequest dbBrandSearch(SearchRequest request) {
    return request;
  }


  /**
   * Gets all Locations from the database, near specific coordinates. It will also filter by the
   * Brand-Names if there are any.
   *
   * @param request The Request Object
   * @return the updated Request Object
   */
  protected SearchRequest getByCoordinates(SearchRequest request) {
    return request;
  }
}
