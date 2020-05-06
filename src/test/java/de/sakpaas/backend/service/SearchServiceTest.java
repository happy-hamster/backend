package de.sakpaas.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.sakpaas.backend.HappyHamsterTest;
import de.sakpaas.backend.exception.EmptySearchQueryException;
import de.sakpaas.backend.model.CoordinateDetails;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.LocationDetails;
import de.sakpaas.backend.model.SearchRequest;
import de.sakpaas.backend.model.SearchResultObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

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
    final Double coordinates = 1.0;

    final SearchRequest request = new SearchRequest();
    request.setCoordinates(new CoordinateDetails(coordinates, coordinates));
    request.setBrands(Collections.singleton("good"));

    final Location location = new Location();
    location.setLatitude(coordinates);
    location.setLongitude(coordinates);
    location.setName("bad");

    final List<Location> locationList = new ArrayList<>(Collections.singletonList(location));

    Mockito.when(locationService.findByCoordinates(coordinates, coordinates))
        .thenReturn(locationList);

    final SearchRequest response = searchService.getByCoordinates(request);

    assertThat(response.getLocations()).isEqualTo(new HashSet<>());
  }

  @Test
  public void testDoubleInsertion() {
    final Double coordinates = 1.0;

    final SearchRequest request = new SearchRequest();
    request.setCoordinates(new CoordinateDetails(coordinates, coordinates));
    request.setBrands(Collections.singleton("good"));

    final LocationDetails locationDetails = new LocationDetails();
    locationDetails.setBrand("good");

    final Location location1 = new Location();
    location1.setLatitude(coordinates);
    location1.setLongitude(coordinates);
    location1.setName("good");
    location1.setDetails(locationDetails);

    final Location location2 = new Location();
    location2.setLatitude(coordinates);
    location2.setLongitude(coordinates);
    location2.setName("good");
    location2.setDetails(locationDetails);

    final List<Location> locationList = new ArrayList<>(Arrays.asList(location1, location2));

    Mockito.when(locationService.findByCoordinates(coordinates, coordinates))
        .thenReturn(locationList);

    final SearchRequest response = searchService.getByCoordinates(request);

    assertThat(response.getLocations().toString())
        .isEqualTo(new HashSet<>(Collections.singleton(location1)).toString());
  }

  @Test
  public void testFilterUnwantedLocationsByBrand() {
    final Double coordinates = 1.0;

    final SearchRequest request = new SearchRequest();
    request.setCoordinates(new CoordinateDetails(coordinates, coordinates));
    request.setBrands(Collections.singleton("good"));

    final LocationDetails badLocationDetails = new LocationDetails();
    badLocationDetails.setBrand("bad");

    final LocationDetails goodLocationDetails = new LocationDetails();
    goodLocationDetails.setBrand("good");

    final Location badLocation = new Location();
    badLocation.setLatitude(coordinates);
    badLocation.setLongitude(coordinates);
    badLocation.setName("bad");
    badLocation.setDetails(badLocationDetails);

    final Location goodLocation = new Location();
    goodLocation.setLatitude(coordinates);
    goodLocation.setLongitude(coordinates);
    goodLocation.setName("good");
    goodLocation.setDetails(goodLocationDetails);

    final List<Location> listContainingAllLocations =
        new ArrayList<>(Arrays.asList(badLocation, goodLocation));

    Mockito.when(locationService.findByCoordinates(coordinates, coordinates))
        .thenReturn(listContainingAllLocations);

    final SearchRequest response = searchService.getByCoordinates(request);

    assertThat(response.getLocations()).isEqualTo(Collections.singleton(goodLocation));
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
        new HashSet<>(
            Collections.singletonList(new Location(1L, "LIDL", 41.0D, 8.0D, null, null))));
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

  @Test
  void createRequestThrowExceptionWithEmptyString() {
    Assertions.assertThrows(
        EmptySearchQueryException.class,
        () -> searchService.createRequest("", new CoordinateDetails(1.0, 1.0)));
  }

  @Test
  void createRequestThrowExceptionWithNullString() {
    Assertions.assertThrows(
        EmptySearchQueryException.class,
        () -> searchService.createRequest(null, new CoordinateDetails(1.0, 1.0)));
  }
  @Test
  void testFilterLocationWithoutNameOrBrandWithoutBrand() {
    final Location location = new Location();
    final List<Location> listWithLocationWithoutName =
        new ArrayList<>(Collections.singletonList(location));

    final Location test1 = new Location();
    test1.setName("test");
    final Location test2 = new Location();
    test1.setName("test");
    final Location test3 = new Location();
    test1.setName("test");

    final SearchRequest searchRequest = new SearchRequest();
    searchRequest.setCoordinates(new CoordinateDetails(1.0, 1.0));
    searchRequest.setLocations(new HashSet<>(Arrays.asList(test1, test2, test3)));

    Mockito.when(locationService.findByCoordinates(1.0, 1.0))
        .thenReturn(listWithLocationWithoutName);

    final SearchRequest response = searchService.getByCoordinates(searchRequest);

    assertThat(response.getLocations().toString())
        .isEqualTo(listWithLocationWithoutName.toString());
  }

  @Test
  void testLocationWithoutNameOrBrand() {
    final Location location = new Location();
    final List<Location> listWithLocationWithoutName =
        new ArrayList<>(Collections.singletonList(location));

    final Location test1 = new Location();
    test1.setName("test");
    final Location test2 = new Location();
    test1.setName("test");
    final Location test3 = new Location();
    test1.setName("test");

    final SearchRequest request = new SearchRequest();
    request.setCoordinates(new CoordinateDetails(1.0, 1.0));
    request.setLocations(new HashSet<>(Arrays.asList(test1, test2, test3)));
    request.setBrands(new HashSet<>(Collections.singleton("brand")));

    Mockito.when(locationService.findByCoordinates(1.0, 1.0))
        .thenReturn(listWithLocationWithoutName);

    final SearchRequest response = searchService.getByCoordinates(request);

    assertThat(response.getLocations().toString()).isEqualTo(new HashSet<>().toString());
  }

  @Test
  void testLocationWithoutName() {
    SearchService.knownBrands = new HashSet<>(Collections.singleton("null"));

    final LocationDetails locationDetails = new LocationDetails();
    locationDetails.setBrand("null");

    final Location location = new Location();
    location.setDetails(locationDetails);
    final List<Location> locationList = new ArrayList<>(Collections.singletonList(location));

    final Location test1 = new Location();
    test1.setName("test1");
    final Location test2 = new Location();
    test1.setName("test2");
    final Location test3 = new Location();
    test1.setName("test3");

    final Set<Location> locationSet =
        new HashSet<>(new ArrayList<>(Arrays.asList(test1, test2, test3)));

    final SearchRequest searchRequest = new SearchRequest();
    searchRequest.setCoordinates(new CoordinateDetails(1.0, 1.0));
    searchRequest.setLocations(locationSet);

    Mockito.when(locationService.findByCoordinates(1.0, 1.0)).thenReturn(locationList);

    final SearchRequest response = searchService.getByCoordinates(searchRequest);

    assertThat(response.getLocations()).isEqualTo(new HashSet<>(Collections.singleton(location)));
  }
}
