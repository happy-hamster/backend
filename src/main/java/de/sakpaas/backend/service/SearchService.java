package de.sakpaas.backend.service;

import de.sakpaas.backend.exception.EmptySearchQueryException;
import de.sakpaas.backend.model.CoordinateDetails;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.LocationDetails;
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
  public SearchResultObject search(String query, CoordinateDetails coordinateDetails,
                                   List<String> type) {
    SearchRequest request = createRequest(query, coordinateDetails);
    if (!request.getQuery().isEmpty()) {
      request = getCoordinatesFromNominatim(request);
    } else {
      if (request.getCoordinates().getLatitude() == null
          || request.getCoordinates().getLongitude() == null) {
        request = dbBrandSearch(request);
        request = filterByType(request, type);
        return new SearchResultObject(request.getCoordinates(), request.getLocations());
      }
    }
    request = getByCoordinates(request);
    request = filterByType(request, type);
    return new SearchResultObject(request.getCoordinates(), request.getLocations());
  }

  /**
   * Filters the the locations by the given location type.
   *
   * @param request The SearchRequest
   * @param type    The list of location types
   * @return The filtered SearchRequest
   */
  protected SearchRequest filterByType(SearchRequest request, List<String> type) {
    Set<Location> locations = request.getLocations();

    // iff type == null or type is an empty array or the location set is empty
    if (type == null || type.isEmpty() || locations.isEmpty()) {
      return request;
    }

    List<String> lowerCaseType =
        type.stream().map(String::toLowerCase).collect(Collectors.toList());

    locations = locations.stream()
        .filter(location -> {
          LocationDetails details = location.getDetails();
          return lowerCaseType.contains(details.getType().toLowerCase());
        }).collect(Collectors.toSet());

    request.setLocations(locations);
    return request;
  }

  /**
   * Creates a new SearchRequest Object. Also makes all query entries lower case.
   *
   * @param query             The SearchQuery
   * @param coordinateDetails The SearchCoordinates
   * @return A new SearchRequest
   * @throws EmptySearchQueryException Iff the query is empty
   */
  protected SearchRequest createRequest(String query, CoordinateDetails coordinateDetails)
      throws EmptySearchQueryException {
    if (query == null || query.isEmpty()) {
      throw new EmptySearchQueryException("The search query is empty");
    }

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
    locations.stream().findAny().ifPresent(result ->
        request.setCoordinates(new CoordinateDetails(result.getLatitude(), result.getLongitude())));
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
    Set<String> brands = request.getBrands();
    CoordinateDetails coordinateDetails = request.getCoordinates();
    List<Location> locations = locationService
        .findByCoordinates(coordinateDetails.getLatitude(), coordinateDetails.getLongitude());

    if (brands != null && !brands.isEmpty()) {
      Set<Location> filteredLocations = new HashSet<>();
      for (String brand : brands) {
        filteredLocations.addAll(locations.stream()
            .filter(location -> filterLocationsByBrand(location, brand))
            .collect(Collectors.toList()));
      }
      request.setLocations(filteredLocations);
    } else {
      request.setLocations(new HashSet<>(locations));
    }

    return request;
  }

  /**
   * Tests whether a location is from a specific brand.
   *
   * @param location The Location in question
   * @param brand    The brand we are filtering with
   * @return Whether The Location is from the brand we are searching for. {@code True} iff the
   *     Location is from a specific brand. {@code False} otherwise.
   */
  protected boolean filterLocationsByBrand(Location location, String brand) {
    final String locationName;
    final LocationDetails details;

    boolean locationNameContainsBrand = false;

    if (location.getName() != null) {
      locationName = location.getName().toLowerCase();
      locationNameContainsBrand = locationName.contains(brand);
    }
    if (location.getDetails() != null) {
      details = location.getDetails();
    } else { // location.getDetails == null
      return locationNameContainsBrand;
    }
    if (details.getBrand() != null) {
      return locationNameContainsBrand || details.getBrand().toLowerCase().equals(brand);
    }
    return false;
  }
}
