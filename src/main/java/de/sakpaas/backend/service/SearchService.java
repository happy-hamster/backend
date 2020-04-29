package de.sakpaas.backend.service;

import de.sakpaas.backend.exception.EmptySearchQueryException;
import de.sakpaas.backend.model.CoordinateDetails;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.SearchRequest;
import de.sakpaas.backend.model.SearchResultObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class SearchService {
  @Value("${app.search-result-limit}")
  private Integer searchResultLimit;

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
    StringBuilder lowerquery = new StringBuilder(query.toLowerCase());
    SearchRequest request = new SearchRequest();
    request.setBrands(new HashSet<>());
    request.setCoordinates(coordinateDetails);
    List<String> brands = new ArrayList<>(knownBrands);

    for (int n = 0; n < brands.size(); n++) {
      String temp = lowerquery.toString();
      if (temp.contains(brands.get(n))) {
        int beginn = lowerquery.indexOf(brands.get(n));
        int end = lowerquery.indexOf(brands.get(n)) + brands.get(n).length();

        if (checkIfValidWord(beginn, end, lowerquery)) {
          lowerquery.replace(beginn, end, "");
          request.getBrands().add(brands.get(n));
          n--;
        }
      }
    }

    String resultQuery = lowerquery.toString();
    resultQuery.trim();

    request.setQuery(new HashSet<>(Arrays.asList(resultQuery.split(" "))).stream()
        .filter(temp -> (!temp.equals(""))).collect(
            Collectors.toSet()));



    /*SearchRequest searchRequest = new SearchRequest();
    HashSet<String> giveQuery = new HashSet<String>();
    HashSet<String> giveBrands = new HashSet<String>();

    String lowerquery = query.toLowerCase();
    ArrayList<String> brandsArray = new ArrayList<String>();
    for (String brand : knownBrands) {
      brandsArray.add(brand);
    }
    Collections.sort(brandsArray, Collections.reverseOrder());

    for (String brand : brandsArray) {
      if (lowerquery.contains(brand)) {
        lowerquery = lowerquery.replace(brand, "");
        giveBrands.add(brand);
      }
    }

    // char space = ' ';
    // for (String s : querysplit) {
    String specialstuff = "!`\"§$%&/()=?#'}][{³²<>|,.;:-_~+*";
    //for (int charindex = 63; charindex < 91; charindex++) { //big letters
    for (int i = 0; i < specialstuff.length(); i++) { //löscht sonderzeichen
      char special = specialstuff.charAt(i);

      //char c = (char) charindex;
      //if (c != space) {
      if (lowerquery.contains("" + special)) {
        lowerquery = lowerquery.replace(special + "", "");
      }
    }


    /**
     * for (int charindex = 97; charindex < 123; charindex++) { //small letters
     * char c = (char) charindex;
     * if (c != space) {
     * if (!lowerquery.contains("" + c)) {
     * lowerquery = lowerquery.replace(c + "", "");
     * }
     * }
     * }
     */
    /*
    String[] querysplit = lowerquery.split(" ");

    for (String s : querysplit) { //recognizes all strings not in giveBrands an add to give
      if (!s.equals("")) {
        if (!giveBrands.contains(s)) {
          giveQuery.add(s);
        }
      }
    }

    searchRequest.setBrands(giveBrands);
    searchRequest.setQuery(giveQuery);
    searchRequest.setCoordinates(coordinateDetails);
    searchRequest.setResultLimit(searchResultLimit);
  */
    return request;
  }

  /**
   * Assisting Method for createRequest.
   *
   * @param beginn The beginning Index of the Word
   * @param end    The ending Index of the Word
   * @param query  The String the word contains
   * @return Returns if word is valid
   */
  private Boolean checkIfValidWord(int beginn, int end, StringBuilder query) {
    if (beginn == 0 || (query.charAt(beginn - 1) == ' ')) {
      return (end == query.length()) || (query.charAt(end) == ' ');
    }
    return false;
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
    return request;
  }
}
