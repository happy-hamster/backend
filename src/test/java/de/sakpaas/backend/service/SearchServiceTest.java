package de.sakpaas.backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import de.sakpaas.backend.HappyHamsterTest;
import de.sakpaas.backend.model.CoordinateDetails;
import de.sakpaas.backend.model.SearchRequest;
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
}