package de.sakpaas.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.sakpaas.backend.HappyHamsterTest;
import de.sakpaas.backend.model.CoordinateDetails;
import de.sakpaas.backend.model.CoordinateDetails;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.LocationDetails;
import de.sakpaas.backend.model.SearchRequest;
import de.sakpaas.backend.model.SearchResultObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
@RunWith(SpringRunner.class)
class SearchServiceTest extends HappyHamsterTest {

  @MockBean
  SearchMappingService searchMappingService;

  @MockBean
  LocationService locationService;
  @Autowired
  SearchService searchService;

  @Test
  void getCoordinatesFromNominatimWithCoordinates() {
    final CoordinateDetails searchCoordinates = new CoordinateDetails(1.0, 1.0);
    final CoordinateDetails resultCoordinates = new CoordinateDetails(2.0, 2.0);
    final Set<String> query = new HashSet<>(Collections.singleton("test"));
    final SearchRequest searchRequest = new SearchRequest();
    searchRequest.setQuery(query);
    searchRequest.setCoordinates(searchCoordinates);

    Mockito.when(searchMappingService.search(query, searchCoordinates))
        .thenReturn(resultCoordinates);

    SearchRequest searchResult = searchService.getCoordinatesFromNominatim(searchRequest);

    assertThat(searchResult.getCoordinates()).isEqualTo(resultCoordinates);
  }

  @Test
  void getCoordinatesFromNominatimWithoutCoordinates() {
    final CoordinateDetails searchCoordinates = new CoordinateDetails(1.0, 1.0);
    final CoordinateDetails resultCoordinates = new CoordinateDetails(2.0, 2.0);
    final Set<String> query = new HashSet<>(Collections.singleton("test"));
    final SearchRequest searchRequest = new SearchRequest();
    searchRequest.setQuery(query);
    searchRequest.setCoordinates(searchCoordinates);

    Mockito.when(searchMappingService.search(query, searchCoordinates))
        .thenThrow(new IndexOutOfBoundsException());
    Mockito.when(searchMappingService.search(query)).thenReturn(resultCoordinates);

    SearchRequest searchResult = searchService.getCoordinatesFromNominatim(searchRequest);

    assertThat(searchResult.getCoordinates()).isEqualTo(resultCoordinates);
  }

  private SearchRequest createSearchRequest(String query) {
    SearchRequest searchRequest = new SearchRequest();
    searchRequest.setQuery(new HashSet<>(Arrays.asList(query.split(" "))));
    return searchRequest;
  }

  private Set<String> getBrandSet() {
    Set<String> brands = new HashSet<>();
    brands.add("lidl");
    brands.add("aldi");
    brands.add("edeka");
    brands.add("deutsche post");
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
  void createRequestWithOnlyBrandsInQuery() {
    SearchService.setKnownBrands(getBrandSet());
    SearchRequest resultRequest =
        searchService.createRequest("Mannheim", new CoordinateDetails(2.0, 3.0));
    assertThat(resultRequest.getQuery().size()).isEqualTo(1);
    assertThat(resultRequest.getQuery().contains("mannheim")).isTrue();
    assertThat(resultRequest.getBrands().size()).isEqualTo(0);
  }

  @Test
  void createRequestWithOnlyBrandInQuery() {
    SearchService.setKnownBrands(getBrandSet());
    SearchRequest resultRequest =
        searchService.createRequest("Lidl", new CoordinateDetails(2.0, 3.0));
    assertThat(resultRequest.getQuery().size()).isEqualTo(0);
    assertThat(resultRequest.getBrands().size()).isEqualTo(1);
    assertThat(resultRequest.getBrands().contains("lidl")).isTrue();
  }

  @Test
  void createRequestWithMultipleBrandsInQuery() {
    SearchService.setKnownBrands(getBrandSet());
    SearchRequest resultRequest =
        searchService.createRequest("lidl edeka aldi aldi", new CoordinateDetails(2.0, 3.0));
    assertThat(resultRequest.getBrands().size()).isEqualTo(3);
    assertThat(resultRequest.getQuery().size()).isEqualTo(0);
    assertThat(resultRequest.getBrands().contains("lidl")).isTrue();
    assertThat(resultRequest.getBrands().contains("edeka")).isTrue();
    assertThat(resultRequest.getBrands().contains("aldi")).isTrue();
  }

  @Test
  void createRequestWithBrandsAndNoneBrandsInQuery() {
    SearchService.setKnownBrands(getBrandSet());
    SearchRequest resultRequest = searchService
        .createRequest("mannheim wasserturm edeka aldi", new CoordinateDetails(2.0, 3.0));
    assertThat(resultRequest.getBrands().size()).isEqualTo(2);
    assertThat(resultRequest.getBrands().contains("edeka")).isTrue();
    assertThat(resultRequest.getBrands().contains("aldi")).isTrue();
    assertThat(resultRequest.getQuery().size()).isEqualTo(2);
    assertThat(resultRequest.getQuery().contains("mannheim")).isTrue();
    assertThat(resultRequest.getQuery().contains("wasserturm")).isTrue();
  }

  @Test
  void createRequestWithSpaceInBrandName() {
    SearchService.setKnownBrands(getBrandSet());
    SearchRequest resultRequest =
        searchService.createRequest("Deutsche Post Mannheim", new CoordinateDetails(2.0, 3.0));
    assertThat(resultRequest.getQuery().size()).isEqualTo(1);
    assertThat(resultRequest.getBrands().size()).isEqualTo(1);
    assertThat(resultRequest.getQuery().contains("mannheim")).isTrue();
    assertThat(resultRequest.getBrands().contains("deutsche post")).isTrue();
  }

  @Test
  void createRequestWithoutCoordinates() {
    SearchService.setKnownBrands(getBrandSet());
    SearchRequest resultRequest = searchService.createRequest("Deutsche Post Mannheim", null);
    assertThat(resultRequest.getCoordinates()).isNull();
  }

  @Test
  void createRequestWithShorterBrandNameInLongerBrand() {
    HashSet<String> brandSet = new HashSet<>();
    brandSet.add("ed");
    SearchService.setKnownBrands(brandSet);
    SearchRequest resultRequest =
        searchService.createRequest("Edeka", new CoordinateDetails(2.0, 3.0));
    assertThat(resultRequest.getQuery().size()).isEqualTo(1);
    assertThat(resultRequest.getBrands().size()).isEqualTo(0);
    assertThat(resultRequest.getQuery().contains("edeka")).isTrue();
  }

  @Test
  void createRequestWithQueryTricksRegex() {
    HashSet<String> brandSet = new HashSet<>();
    brandSet.add("bioladen*");
    SearchService.setKnownBrands(brandSet);

    SearchRequest resultRequest =
        searchService.createRequest("Bioladen*", new CoordinateDetails(2.0, 3.0));
    assertThat(resultRequest.getQuery().size()).isEqualTo(0);
    assertThat(resultRequest.getBrands().size()).isEqualTo(1);

    resultRequest =
        searchService.createRequest("Biolade", new CoordinateDetails(2.0, 3.0));
    assertThat(resultRequest.getQuery().size()).isEqualTo(1);
    assertThat(resultRequest.getBrands().size()).isEqualTo(0);
    assertThat(resultRequest.getQuery().contains("biolade"));


  }

  @Test
  void createRequestWithQueryTricksRegexV2() {
    HashSet<String> brandSet = new HashSet<>();
    brandSet.add("[: bioladen");
    SearchService.setKnownBrands(brandSet);

    SearchRequest resultRequest =
        searchService.createRequest("[: Bioladen", new CoordinateDetails(2.0, 3.0));
    assertThat(resultRequest.getQuery().size()).isEqualTo(0);
    assertThat(resultRequest.getBrands().size()).isEqualTo(1);
    assertThat(resultRequest.getBrands().contains("[: bioladen"));
  }

  @Test
  void searchWithEmptyCoordinatesAndOnlyBrands() {
    SearchService mockSearchService = Mockito.spy(searchService);

    SearchRequest request = new SearchRequest();
    request.setQuery(new HashSet<>());
    request.setCoordinates(new CoordinateDetails(null, null));
    Mockito.doReturn(request).when(mockSearchService).createRequest(Mockito.any(), Mockito.any());

    request.setLocations(
        new HashSet<>(Arrays.asList(new Location(1L, "LIDL", 41.0D, 8.0D, null, null))));
    Mockito.doReturn(request).when(mockSearchService).dbBrandSearch(Mockito.any());

    SearchResultObject result =
        mockSearchService.search("testQuery", new CoordinateDetails(null, null));
    assertThat(result.getLocationList().size()).isEqualTo(1);
    assertThat(result.getCoordinates().getLatitude()).isNull();
    assertThat(result.getCoordinates().getLongitude()).isNull();
    //Count Method interactions
    verify(mockSearchService, times(1)).createRequest(Mockito.any(), Mockito.any());
    verify(mockSearchService, times(1)).dbBrandSearch(Mockito.any());
    verify(mockSearchService, times(0)).getCoordinatesFromNominatim(Mockito.any());
    verify(mockSearchService, times(0)).getCoordinatesFromNominatim(Mockito.any());
  }
}
