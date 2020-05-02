package de.sakpaas.backend.service;

import de.sakpaas.backend.exception.EmptySearchQueryException;
import de.sakpaas.backend.model.CoordinateDetails;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.SearchRequest;
import de.sakpaas.backend.model.SearchResultObject;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class SearchService {
  private static final Logger LOGGER = LoggerFactory.getLogger(SearchService.class);
  @Setter
  protected static Set<String> knownBrands;
  private final LocationService locationService;
  private final SearchMappingService searchMappingService;
  private final LocationDetailsRepository locationDetailsRepository;
  @Value("${app.search-result-limit}")
  private Integer searchResultLimit;

  /**
   * Searches for a specific key, calculates the central point as coordinates and returns
   * additionally a list of locations around the coordinates.
   *
   * @param locationService           The service for getting locations based on specific
   *                                  coordinates
   * @param searchMappingService      The SearchMapping Service
   * @param locationDetailsRepository The Location Details Repository
   */
  @Autowired
  public SearchService(LocationService locationService,
                       SearchMappingService searchMappingService,
                       LocationDetailsRepository locationDetailsRepository) {
    this.locationService = locationService;
    this.searchMappingService = searchMappingService;
    this.locationDetailsRepository = locationDetailsRepository;
    updateBrands();
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
    SearchRequest request = createRequest(query, coordinateDetails);
    if (!request.getQuery().isEmpty()) {
      request = getCoordinatesFromNominatim(request);
    } else {
      if (request.getCoordinates().getLatitude() == null
          || request.getCoordinates().getLongitude() == null) {
        request = dbBrandSearch(request);
        return new SearchResultObject(request.getCoordinates(), request.getLocations());
      }
    }
    request = getByCoordinates(request);
    return new SearchResultObject(request.getCoordinates(), request.getLocations());
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
    query = query.toLowerCase();

    // Stores all brands found in the query
    Set<String> brands = new HashSet<>();

    // Iterate over all brands, starting from the longest
    for (String brand : knownBrands.stream()
        .sorted((b1, b2) -> Integer.compare(b2.length(), b1.length()))
        .collect(Collectors.toList())) {
      // Matches all brands preceded ( ?> ) with the string start ( ^ ) or any whitespace ( \s )
      // and trailed ( ?= ) by the string end ( $ ) or any whitespace ( \s )
      String regex = "(?>^|\\s)(" + Pattern.quote(brand) + ")(?=\\s|$)";
      Matcher matcher = Pattern.compile(regex).matcher(query);
      // Check if the brand can be found
      if (matcher.find()) {
        // Add the brand
        brands.add(brand);
        // Remove the brand from the query
        query = matcher.replaceAll(" ");
      }
    }

    SearchRequest request = new SearchRequest();
    request.setBrands(brands);
    request.setCoordinates(coordinateDetails);
    request.setResultLimit(searchResultLimit);
    request.setQuery(
        // Find all words (separated by any whitespace ( \s )) in query and remove empty words
        Arrays.stream(query.split("\\s"))
            .filter(temp -> !temp.isEmpty())
            .collect(Collectors.toSet())
    );
    return request;
  }

  /**
   * Creates a Nominatim Request and executes it. It updates the Coordinates of the Request.
   *
   * @param request The Request Object
   * @return the updated Request Object
   */
  protected SearchRequest getCoordinatesFromNominatim(SearchRequest request) {
    CoordinateDetails coordinateDetails;

    if (request.getCoordinates().getLongitude() != null) {
      try {
        coordinateDetails =
            searchMappingService.search(request.getQuery(), request.getCoordinates());
      } catch (IndexOutOfBoundsException e) {
        coordinateDetails = searchMappingService.search(request.getQuery());
      }
    } else {
      coordinateDetails = searchMappingService.search(request.getQuery());
    }
    request.setCoordinates(coordinateDetails);

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
    Set<String> brands = request.getBrands();
    Set<Location> locations = request.getLocations();
    if (locations == null) {
      locations = new HashSet<>();
    }

    for (String brand : brands) {
      if (!brand.equals("")) {
        brand = "%" + brand + "%";
        locations.addAll(
            locationService.findByNameOrBrandLike(brand, request.getResultLimit()));
      }
    }
    request.setLocations(locations);
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
    // Get Locations
    CoordinateDetails coordinateDetails = request.getCoordinates();
    List<Location> locations = locationService
        .findByCoordinates(coordinateDetails.getLatitude(), coordinateDetails.getLongitude());
    // Filter by brand
    if (!knownBrands.isEmpty()) {
      locations = locations.stream()
          .filter(location -> {
            for (String brand : knownBrands) {
              if (location.getName() == null || location.getDetails() == null ||
                  location.getDetails().getBrand() == null) {
                if (location.getName() != null) {
                  return location.getName().contains(brand);
                } else if (location.getDetails() != null) {
                  if (location.getDetails().getBrand() != null) {
                    return location.getDetails().getBrand().equals(brand);
                  }
                }
              } else if (location.getName().contains(brand) ||
                  location.getDetails().getBrand().equals(brand)) {
                return true;
              }
            }
            return false;
          }).collect(Collectors.toList());
      request.setLocations(new HashSet<>(locations));
    } else {
      request.setLocations(new HashSet<>());
    }

    return request;
  }
}
