package de.sakpaas.backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import de.sakpaas.backend.model.SearchRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

class SearchServiceTest {

  @Autowired
  SearchService searchService;

  private SearchRequest createSearchRequest(String query) {
    SearchRequest searchRequest = new SearchRequest();
    searchRequest.setQuery(new ArrayList<>(Arrays.asList(query.split(" "))));
    return searchRequest;
  }

  private List<String> getBrandList() {
    List<String> brands = new ArrayList<>();
    brands.add("Lidl");
    brands.add("Aldi");
    brands.add("Edeka");
    return brands;
  }

  @Test
  void checkForBrandsWithoutBrandsInQuery() {
    SearchService.setKnownBrands(getBrandList());
    SearchRequest resultRequest = searchService.checkForBrands(createSearchRequest("Mannheim"));
    assertThat(resultRequest.getQuery().size()).isEqualTo(1);
    assertThat(resultRequest.getQuery().contains("Mannheim")).isTrue();
    assertThat(resultRequest.getBrands().size()).isEqualTo(0);
  }

  @Test
  void checkForBrandsWithOnlyBrandInQuery() {
    SearchService.setKnownBrands(getBrandList());
    SearchRequest resultRequest = searchService.checkForBrands(createSearchRequest("Lidl"));
    assertThat(resultRequest.getQuery().size()).isEqualTo(0);
    assertThat(resultRequest.getBrands().size()).isEqualTo(1);
    assertThat(resultRequest.getBrands().contains("Lidl")).isTrue();
  }

  @Test
  void checkForBrandsWithMultipleBrandsInQuery() {
    SearchService mockSearchService = Mockito.spy(searchService);
    Mockito.doReturn(getBrandList()).when(searchService).getKnownBrands();
    SearchRequest resultRequest =
        mockSearchService.checkForBrands(createSearchRequest("Lidl Edeka Aldi"));
    assertThat(resultRequest.getBrands().size()).as("").isEqualTo(3);
    assertThat(resultRequest.getQuery().size()).isEqualTo(0);
    assertThat(resultRequest.getBrands().contains("Lidl")).isTrue();
    assertThat(resultRequest.getBrands().contains("Edeka")).isTrue();
    assertThat(resultRequest.getBrands().contains("Aldi")).isTrue();
  }

  @Test
  void checkForBrandsWithBrandsAndNoneBrands() {
    SearchService mockSearchService = Mockito.spy(searchService);
    Mockito.doReturn(getBrandList()).when(searchService).getKnownBrands();
    SearchRequest resultRequest =
        mockSearchService.checkForBrands(createSearchRequest("Mannheim Wasserturm Edeka Aldi"));
    assertThat(resultRequest.getBrands().size()).isEqualTo(2);
    assertThat(resultRequest.getBrands().contains("Edeka")).isTrue();
    assertThat(resultRequest.getBrands().contains("Aldi")).isTrue();
    assertThat(resultRequest.getQuery().size()).isEqualTo(2);
    assertThat(resultRequest.getQuery().contains("Mannheim")).isTrue();
    assertThat(resultRequest.getQuery().contains("Wasserturm")).isTrue();
  }

  void checkForBrandsWithDublicatedBrands() {
    SearchService mockSearchService = Mockito.spy(searchService);
    Mockito.doReturn(getBrandList()).when(searchService).getKnownBrands();
    SearchRequest resultRequest =
        mockSearchService.checkForBrands(createSearchRequest("Lidl Lidl"));
    assertThat(resultRequest.getBrands().size()).isEqualTo(1);
    assertThat(resultRequest.getBrands().contains("Lidl")).isTrue();
  }
}