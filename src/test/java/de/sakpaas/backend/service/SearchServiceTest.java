package de.sakpaas.backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import de.sakpaas.backend.HappyHamsterTest;
import de.sakpaas.backend.model.Address;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.LocationDetails;
import de.sakpaas.backend.model.SearchRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SearchServiceTest extends HappyHamsterTest {

  @Autowired
  private SearchService searchService;
  
  @Autowired
  private LocationRepository locationRepository;
  
  @Autowired
  private LocationDetailsRepository locationDetailsRepository;
  
  @Autowired
  private AddressRepository addressRepository;

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
  
  /**
   * Integration Test (dbBrandSearch in {@link SearchService.java})
   */
  @Test
  void bla() {
    locationDetailsRepository.deleteAll();
    locationRepository.deleteAll();
    addressRepository.deleteAll();
    
    
    LocationDetails locd1 = new LocationDetails("supermarket", "irrelevant", "Shop");
    LocationDetails locd2 = new LocationDetails("supermarket", "irrelevant", "Aldi");
    LocationDetails locd3 = new LocationDetails("supermarket", "irrelevant", "Lidl");
    LocationDetails locd4 = new LocationDetails("supermarket", "irrelevant", "Lidl");
    locationDetailsRepository.save(locd1);
    locationDetailsRepository.save(locd2);
    locationDetailsRepository.save(locd3);
    locationDetailsRepository.save(locd4);
    
    Address address1 = new Address("A", "B", "C", "D", "E");
    Address address2 = new Address("A", "B", "C", "D", "E");
    Address address3 = new Address("A", "B", "C", "D", "E");
    Address address4 = new Address("A", "B", "C", "D", "E");
    addressRepository.save(address1);
    addressRepository.save(address2);
    addressRepository.save(address3);
    addressRepository.save(address4);
    
    Location loc1 = new Location(1L, "Test Lidl", 4.0, 4.0, locd1, address1);
    Location loc2 = new Location(2L, "Test Aldi", 4.0, 4.0, locd2, address2);
    Location loc3 = new Location(3L, "Test Supermarket", 4.0, 4.0, locd3, address3);
    Location loc4 = new Location(4L, "Test Lidl", 4.0, 4.0, locd4, address4);
    locationRepository.save(loc1);
    locationRepository.save(loc2);
    locationRepository.save(loc3);
    locationRepository.save(loc4);
    
    SearchRequest searchRequest = createSearchRequest("Lidl");
    Set<String> brands = new HashSet<String>();
    brands.add("lidl");
    searchRequest.setBrands(brands);
    searchRequest.setResultLimit(2);
    
    
    Set<Location> locations = searchService.dbBrandSearch(searchRequest).getLocations();
    assertThat(locations.size()).isEqualTo(2);
    
    searchRequest.getLocations().clear();
    searchRequest.setResultLimit(3);
    
    Set<Location> locations2 = searchService.dbBrandSearch(searchRequest).getLocations();
    assertThat(locations2.size()).isEqualTo(3);
    
    searchRequest.getLocations().clear();
    brands.clear();
    brands.add("");
    searchRequest.setBrands(brands);
    
    Set<Location> locations3 = searchService.dbBrandSearch(searchRequest).getLocations();
    System.out.println(locations3.size());
    assertThat(locations3.size()).isEqualTo(0);
    
    locationRepository.deleteAll();
    addressRepository.deleteAll();
    locationDetailsRepository.deleteAll();
  }
}