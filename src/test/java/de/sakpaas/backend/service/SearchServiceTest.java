package de.sakpaas.backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import de.sakpaas.backend.HappyHamsterTest;
import de.sakpaas.backend.model.CoordinateDetails;
import de.sakpaas.backend.model.Location;
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
  public void testFilterUnwantedLocations() {
    final String[] knownBrands = {"1"};
    final CoordinateDetails coordinateDetails = new CoordinateDetails(1, 1);
    final SearchRequest searchRequest = new SearchRequest();
    searchRequest.setCoordinates(coordinateDetails);
    searchService.setKnownBrands(Arrays.asList(knownBrands));

    final Location location = new Location();
    location.setLatitude(1.0);
    location.setLongitude(1.0);
    location.setName("2");
    final List<Location> locationList = new ArrayList<>(Collections.singletonList(location));

    // mock LocationService
    Mockito.when(locationService.findByCoordinates(1.0, 1.0)).thenReturn(locationList);

    final SearchRequest searchRequest1 = searchService.getByCoordinates(searchRequest);

    assertThat(searchRequest1.getLocations()).isEqualTo(new ArrayList<>());
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