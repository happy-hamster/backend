package de.sakpaas.backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import de.sakpaas.backend.HappyHamsterTest;
import de.sakpaas.backend.model.CoordinateDetails;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.LocationDetails;
import de.sakpaas.backend.model.SearchRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class SearchServiceTest extends HappyHamsterTest {

  @MockBean
  LocationService locationService;

  @Autowired
  private SearchService searchService;


  private SearchRequest createSearchRequest(String query) {
    SearchRequest searchRequest = new SearchRequest();
    searchRequest.setQuery(new HashSet<>(Arrays.asList(query.split(" "))));
    return searchRequest;
  }

  private Set<String> getBrandList() {
    Set<String> brands = new HashSet<>();
    brands.add("lidl");
    brands.add("aldi");
    brands.add("edeka");
    return brands;
  }

  @Test
  public void testFilterUnwantedLocationsByName() {
    final Set<String> knownBrands = Collections.singleton("1");
    final CoordinateDetails coordinateDetails = new CoordinateDetails(1.0, 1.0);
    final SearchRequest searchRequest = new SearchRequest();
    searchRequest.setCoordinates(coordinateDetails);
    SearchService.setKnownBrands(knownBrands);
    final LocationDetails locationDetails = new LocationDetails();
    locationDetails.setBrand("");
    final Location location = new Location();
    location.setLatitude(1.0);
    location.setLongitude(1.0);
    location.setName("2");
    location.setDetails(locationDetails);
    final List<Location> locationList = new ArrayList<>(Collections.singletonList(location));
    // mock LocationService
    Mockito.when(locationService.findByCoordinates(1.0, 1.0)).thenReturn(locationList);
    final SearchRequest searchRequest1 = searchService.getByCoordinates(searchRequest);
    assertThat(searchRequest1.getLocations()).isEqualTo(new HashSet<>());
  }

  @Test
  public void testDoubleInsertion() {
    final Set<String> knownBrands = Collections.singleton("1");
    final CoordinateDetails coordinateDetails = new CoordinateDetails(1.0, 1.0);
    final SearchRequest searchRequest = new SearchRequest();
    searchRequest.setCoordinates(coordinateDetails);
    SearchService.setKnownBrands(knownBrands);
    final LocationDetails locationDetails = new LocationDetails();
    locationDetails.setBrand("1");
    final Location location = new Location();
    location.setLatitude(1.0);
    location.setLongitude(1.0);
    location.setName("1");
    location.setDetails(locationDetails);
    final List<Location> locationList = new ArrayList<>(Collections.singletonList(location));
    // mock LocationService
    Mockito.when(locationService.findByCoordinates(1.0, 1.0)).thenReturn(locationList);
    final SearchRequest searchRequest1 = searchService.getByCoordinates(searchRequest);
    assertThat(searchRequest1.getLocations().toString())
        .isEqualTo(locationList.toString());
  }

  @Test
  public void testFilterUnwantedLocationsByBrand() {
    final Set<String> knownBrands = Collections.singleton("wanted Brand");
    final CoordinateDetails coordinateDetails = new CoordinateDetails(1.0, 1.0);
    final SearchRequest searchRequest = new SearchRequest();
    searchRequest.setCoordinates(coordinateDetails);
    SearchService.setKnownBrands(knownBrands);
    final LocationDetails locationDetails = new LocationDetails();
    locationDetails.setBrand("unwanted Brand");
    final LocationDetails locationDetails1 = new LocationDetails();
    locationDetails1.setBrand("wanted Brand");
    final Location location = new Location();
    location.setLatitude(1.0);
    location.setLongitude(1.0);
    location.setName("unwanted");
    location.setDetails(locationDetails);
    final Location location1 = new Location();
    location1.setLatitude(1.0);
    location1.setLongitude(1.0);
    location1.setName("wanted");
    location1.setDetails(locationDetails1);
    final List<Location> locationList = new ArrayList<>(Arrays.asList(location, location1));
    // mock LocationService
    Mockito.when(locationService.findByCoordinates(1.0, 1.0)).thenReturn(locationList);
    final SearchRequest searchRequest1 = searchService.getByCoordinates(searchRequest);
    assertThat(searchRequest1.getLocations()).isEqualTo(Collections.singleton(location1));
  }

  @Test
  public void testEmptyKnownBrands() {
    final CoordinateDetails coordinateDetails = new CoordinateDetails(1.0, 1.0);
    final SearchRequest searchRequest = new SearchRequest();
    searchRequest.setCoordinates(coordinateDetails);
    SearchService.setKnownBrands(new HashSet<>());
    final Location location = new Location();
    location.setLatitude(1.0);
    location.setLongitude(1.0);
    searchRequest.setLocations(Collections.singleton(location));
    // mock LocationService
    Mockito.when(locationService.findByCoordinates(1.0, 1.0)).thenReturn(new ArrayList<>());
    final SearchRequest searchRequest1 = searchService.getByCoordinates(searchRequest);
    assertThat(searchRequest1.getLocations()).isEqualTo(new HashSet<>());
  }

  @Test
  void checkForBrandsWithoutBrandsInQuery() {
    SearchService.setKnownBrands(getBrandList());
    SearchRequest resultRequest = searchService.checkForBrands(createSearchRequest("mannheim"));
    assertThat(resultRequest.getQuery().size()).isEqualTo(1);
    assertThat(resultRequest.getQuery().contains("mannheim")).isTrue();
    assertThat(resultRequest.getBrands().size()).isEqualTo(0);
  }

  @Test
  void checkForBrandsWithOnlyBrandInQuery() {
    SearchService.setKnownBrands(getBrandList());
    SearchRequest resultRequest = searchService.checkForBrands(createSearchRequest("lidl"));
    assertThat(resultRequest.getQuery().size()).isEqualTo(0);
    assertThat(resultRequest.getBrands().size()).isEqualTo(1);
    assertThat(resultRequest.getBrands().contains("lidl")).isTrue();
  }

  @Test
  void checkForBrandsWithMultipleBrandsInQuery() {
    SearchService.setKnownBrands(getBrandList());
    SearchRequest resultRequest =
        searchService.checkForBrands(createSearchRequest("lidl edeka aldi"));
    assertThat(resultRequest.getBrands().size()).isEqualTo(3);
    assertThat(resultRequest.getQuery().size()).isEqualTo(0);
    assertThat(resultRequest.getBrands().contains("lidl")).isTrue();
    assertThat(resultRequest.getBrands().contains("edeka")).isTrue();
    assertThat(resultRequest.getBrands().contains("aldi")).isTrue();
  }

  @Test
  void checkForBrandsWithBrandsAndNoneBrandsInQuery() {
    SearchService.setKnownBrands(getBrandList());
    SearchRequest resultRequest =
        searchService.checkForBrands(createSearchRequest("mannheim wasserturm edeka aldi"));
    assertThat(resultRequest.getBrands().size()).isEqualTo(2);
    assertThat(resultRequest.getBrands().contains("edeka")).isTrue();
    assertThat(resultRequest.getBrands().contains("aldi")).isTrue();
    assertThat(resultRequest.getQuery().size()).isEqualTo(2);
    assertThat(resultRequest.getQuery().contains("mannheim")).isTrue();
    assertThat(resultRequest.getQuery().contains("wasserturm")).isTrue();
  }

  @Test
  void checkForBrandsWithDublicatedBrandsInQuery() {
    SearchService.setKnownBrands(getBrandList());
    SearchRequest resultRequest =
        searchService.checkForBrands(createSearchRequest("lidl lidl"));
    assertThat(resultRequest.getBrands().size()).isEqualTo(1);
    assertThat(resultRequest.getBrands().contains("lidl")).isTrue();
  }
}