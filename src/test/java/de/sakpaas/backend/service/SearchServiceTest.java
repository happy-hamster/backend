package de.sakpaas.backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import de.sakpaas.backend.HappyHamsterTest;
import de.sakpaas.backend.model.CoordinateDetails;
import de.sakpaas.backend.model.SearchRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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

  @Autowired
  SearchService searchService;

  @Test
  void getCoordinatesFromNominatimWithCoordinates() {
    final CoordinateDetails searchCoordinates = new CoordinateDetails(1.0, 1.0);
    final CoordinateDetails resultCoordinates = new CoordinateDetails(2.0, 2.0);
    final String query = "test";
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
    final String query = "test";
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
}