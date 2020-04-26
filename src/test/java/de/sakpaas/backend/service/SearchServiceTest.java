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
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SearchServiceTest extends HappyHamsterTest {

  @MockBean
  private LocationService locationService;

  @Autowired
  private SearchService searchService;

  @Test
  public void testFilterUnwantedLocationsByName() {
    final String[] knownBrands = {"1"};
    final CoordinateDetails coordinateDetails = new CoordinateDetails(1, 1);
    final SearchRequest searchRequest = new SearchRequest();
    searchRequest.setCoordinates(coordinateDetails);
    searchService.setKnownBrands(Arrays.asList(knownBrands));

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

    assertThat(searchRequest1.getLocations()).isEqualTo(new ArrayList<>());
  }

  @Test
  public void testFilterUnwantedLocationsByBrand() {
    final String[] knownBrands = {"1"};
    final CoordinateDetails coordinateDetails = new CoordinateDetails(1, 1);
    final SearchRequest searchRequest = new SearchRequest();
    searchRequest.setCoordinates(coordinateDetails);
    searchService.setKnownBrands(Arrays.asList(knownBrands));

    final LocationDetails locationDetails = new LocationDetails();
    locationDetails.setBrand("2");

    final LocationDetails locationDetails1 = new LocationDetails();
    locationDetails1.setBrand("1");

    final Location location = new Location();
    location.setLatitude(1.0);
    location.setLongitude(1.0);
    location.setName("2");
    location.setDetails(locationDetails);

    final Location location1 = new Location();
    location1.setLatitude(1.0);
    location1.setLongitude(1.0);
    location1.setName("2");
    location1.setDetails(locationDetails1);
    final List<Location> locationList = new ArrayList<>(Arrays.asList(location, location1));

    // mock LocationService
    Mockito.when(locationService.findByCoordinates(1.0, 1.0)).thenReturn(locationList);

    final SearchRequest searchRequest1 = searchService.getByCoordinates(searchRequest);

    assertThat(searchRequest1.getLocations()).isEqualTo(Collections.singletonList(location1));
  }

  @Test
  public void testEmptyKnownBrands() {
    final CoordinateDetails coordinateDetails = new CoordinateDetails(1, 1);
    final SearchRequest searchRequest = new SearchRequest();
    searchRequest.setCoordinates(coordinateDetails);
    searchService.setKnownBrands(new ArrayList<>());

    final Location location = new Location();
    location.setLatitude(1.0);
    location.setLongitude(1.0);
    searchRequest.setLocations(Collections.singletonList(location));

    // mock LocationService
    Mockito.when(locationService.findByCoordinates(1.0, 1.0)).thenReturn(new ArrayList<>());

    final SearchRequest searchRequest1 = searchService.getByCoordinates(searchRequest);

    assertThat(searchRequest1.getLocations()).isEqualTo(new ArrayList<>());
  }
}