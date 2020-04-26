package de.sakpaas.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.sakpaas.backend.exception.EmptySearchQueryException;
import de.sakpaas.backend.model.SearchRequest;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

class SearchServiceTest {

  @Autowired
  SearchService searchService;

  private SearchRequest createSearchRequest(String query) {
    SearchRequest searchRequest = new SearchRequest();
    searchRequest.setQuery(query);
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
  void checkForBrandsAndExpectException() {
    SearchService mockSearchService = Mockito.spy(searchService);
    Mockito.doReturn(getBrandList()).when(searchService).getKnownBrands();
    assertThrows(EmptySearchQueryException.class, () -> {
      mockSearchService.checkForBrands(createSearchRequest(""));
    });

    assertThrows(EmptySearchQueryException.class, () -> {
      mockSearchService.checkForBrands(createSearchRequest(" "));
    });
  }

  @Test
  void checkForBrandsWithoutBrandsInQuery() {
    SearchService mockSearchService = Mockito.spy(searchService);
    Mockito.doReturn(getBrandList()).when(searchService).getKnownBrands();
    SearchRequest resultRequest = mockSearchService.checkForBrands(createSearchRequest("Mannheim"));
    assertThat(resultRequest.getBrands().size()).isEqualTo(0);
  }

  @Test
  void checkForBrandsWithOnlyBrandInQuery() {
    SearchService mockSearchService = Mockito.spy(searchService);
    Mockito.doReturn(getBrandList()).when(searchService).getKnownBrands();
    SearchRequest resultRequest = mockSearchService.checkForBrands(createSearchRequest("Lidl"));
    assertThat(resultRequest.getBrands().size()).as("").isEqualTo(1);
    assertThat(resultRequest.getBrands().get(0)).isEqualTo("Lidl");
  }

  @Test
  void checkForBrandsWithMultipleBrandsInQuery() {
    SearchService mockSearchService = Mockito.spy(searchService);
    Mockito.doReturn(getBrandList()).when(searchService).getKnownBrands();
    SearchRequest resultRequest =
        mockSearchService.checkForBrands(createSearchRequest("Lidl Edeka Aldi"));
    assertThat(resultRequest.getBrands().size()).as("").isEqualTo(3);
    assertThat(resultRequest.getBrands().get(0)).isEqualTo("Lidl");
    assertThat(resultRequest.getBrands().get(1)).isEqualTo("Edeka");
    assertThat(resultRequest.getBrands().get(2)).isEqualTo("Aldi");
  }
}