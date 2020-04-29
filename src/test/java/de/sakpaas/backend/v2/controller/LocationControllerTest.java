package de.sakpaas.backend.v2.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.sakpaas.backend.HappyHamsterTest;
import de.sakpaas.backend.model.CoordinateDetails;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.SearchResultObject;
import de.sakpaas.backend.service.SearchService;
import de.sakpaas.backend.v2.dto.SearchResultDto;
import de.sakpaas.backend.v2.mapper.SearchResultMapper;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
//@WebMvcTest(LocationController.class)
@AutoConfigureMockMvc
@EnableConfigurationProperties
class LocationControllerTest extends HappyHamsterTest {

  @MockBean
  SearchResultMapper searchResultMapper;
  @Autowired
  private MockMvc mvc;
  @MockBean
  private SearchService searchService;

  private SearchResultObject getExampleSearchResult() {
    List<Location> locationList = new ArrayList<>();
    locationList.add(new Location(1L, "LIDL", 41.0D, 8.0D, null, null));
    locationList.add(new Location(2L, "ALDI", 42.0D, 9.0D, null, null));
    return new SearchResultObject(new CoordinateDetails(49.0, 9.0), locationList);
  }

  @SneakyThrows
  @Test
  void searchForLocationsTestWithOnlyLatOrOnlyLong() {
    mvc.perform(get("/v2/locations/search?query=Mannheim&latitude=44.000000")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    mvc.perform(get("/v2/locations/search?query=Mannheim&longitude=44.000000")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @SneakyThrows
  @Test
  void searchForLocationsTestWithQueryAndCoordinates() {


    Mockito.when(searchService.search("Mannheim", null))
        .thenReturn(getExampleSearchResult());


    Mockito.when(searchResultMapper.mapSearchResultToOutputDto(getExampleSearchResult()))
        .thenReturn(new SearchResultDto());

    mvc.perform(get("/v2/locations/search?query=Mannheim")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());


  }
}