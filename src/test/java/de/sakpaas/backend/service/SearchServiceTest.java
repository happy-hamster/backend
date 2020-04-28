package de.sakpaas.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.sakpaas.backend.HappyHamsterTest;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.SearchRequest;
import de.sakpaas.backend.model.SearchResultObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SearchServiceTest extends HappyHamsterTest {

  @Autowired
  SearchService searchService;

  private SearchRequest createSearchRequest(String query) {
    SearchRequest searchRequest = new SearchRequest();
    searchRequest.setQuery(new HashSet<>(Arrays.asList(query.split(" "))));
    return searchRequest;
  }

  private List<String> getBrandList() {
    List<String> brands = new ArrayList<>();
    brands.add("lidl");
    brands.add("aldi");
    brands.add("edeka");
    return brands;
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

  @Test
  void searchWithEmptyCoordinatesAndOnlyBrands() {
    SearchService mockSearchService = Mockito.spy(searchService);

    SearchRequest request = new SearchRequest();
    request.setQuery(new HashSet<>());
    Mockito.doReturn(request).when(mockSearchService).createRequest(Mockito.any(), Mockito.any());

    request.setLocations(
        new HashSet<>(Arrays.asList(new Location(1L, "LIDL", 41.0D, 8.0D, null, null))));
    Mockito.doReturn(request).when(mockSearchService).dbBrandSearch(Mockito.any());

    SearchResultObject result = mockSearchService.search("testQuery", null);
    assertThat(result.getLocationList().size()).isEqualTo(1);
    assertThat(result.getCoordinates()).isNull();
    //Count Method interactions
    verify(mockSearchService, times(1)).createRequest(Mockito.any(), Mockito.any());
    verify(mockSearchService, times(1)).dbBrandSearch(Mockito.any());
    verify(mockSearchService, times(0)).getCoordinatesFromNominatim(Mockito.any());
    verify(mockSearchService, times(0)).getCoordinatesFromNominatim(Mockito.any());
  }
}