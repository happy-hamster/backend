package de.sakpaas.backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import de.sakpaas.backend.HappyHamsterTest;
import de.sakpaas.backend.model.SearchRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Test;
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
    SearchService.setKnownBrands(getBrandList());
    SearchRequest resultRequest =
        searchService.checkForBrands(createSearchRequest("Lidl Edeka Aldi"));
    assertThat(resultRequest.getBrands().size()).as("").isEqualTo(3);
    assertThat(resultRequest.getQuery().size()).isEqualTo(0);
    assertThat(resultRequest.getBrands().contains("Lidl")).isTrue();
    assertThat(resultRequest.getBrands().contains("Edeka")).isTrue();
    assertThat(resultRequest.getBrands().contains("Aldi")).isTrue();
  }

  @Test
  void checkForBrandsWithBrandsAndNoneBrands() {
    SearchService.setKnownBrands(getBrandList());
    SearchRequest resultRequest =
        searchService.checkForBrands(createSearchRequest("Mannheim Wasserturm Edeka Aldi"));
    assertThat(resultRequest.getBrands().size()).isEqualTo(2);
    assertThat(resultRequest.getBrands().contains("Edeka")).isTrue();
    assertThat(resultRequest.getBrands().contains("Aldi")).isTrue();
    assertThat(resultRequest.getQuery().size()).isEqualTo(2);
    assertThat(resultRequest.getQuery().contains("Mannheim")).isTrue();
    assertThat(resultRequest.getQuery().contains("Wasserturm")).isTrue();
  }

  void checkForBrandsWithDublicatedBrands() {
    SearchService.setKnownBrands(getBrandList());
    SearchRequest resultRequest =
        searchService.checkForBrands(createSearchRequest("Lidl Lidl"));
    assertThat(resultRequest.getBrands().size()).isEqualTo(1);
    assertThat(resultRequest.getBrands().contains("Lidl")).isTrue();
  }
}