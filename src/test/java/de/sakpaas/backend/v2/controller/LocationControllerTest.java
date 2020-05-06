package de.sakpaas.backend.v2.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.sakpaas.backend.HappyHamsterTest;
import de.sakpaas.backend.model.CoordinateDetails;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.SearchResultObject;
import de.sakpaas.backend.service.LocationService;
import de.sakpaas.backend.service.SearchService;
import de.sakpaas.backend.v2.dto.LocationResultLocationDto;
import de.sakpaas.backend.v2.dto.SearchResultDto;
import de.sakpaas.backend.v2.mapper.SearchResultMapper;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@EnableConfigurationProperties
class LocationControllerTest extends HappyHamsterTest {

  @MockBean
  SearchResultMapper searchResultMapper;
  @MockBean
  LocationService locationService;
  @Autowired
  private MockMvc mvc;
  @MockBean
  private SearchService searchService;

  private SearchResultObject getExampleSearchResult() {
    Set<Location> locationList = new HashSet<>();
    locationList.add(new Location(1L, "LIDL", 41.0D, 8.0D, null, null));
    locationList.add(new Location(2L, "ALDI", 42.0D, 9.0D, null, null));
    return new SearchResultObject(new CoordinateDetails(49.0, 9.0), locationList);
  }

  @SneakyThrows
  @Test
  void searchForLocationsTestWithQuery() {
    Mockito.when(searchService.search("Mannheim", new CoordinateDetails(null, null)))
        .thenReturn(getExampleSearchResult());

    SearchResultDto searchResultDto = new SearchResultDto(
        new LocationResultLocationDto.LocationResultCoordinatesDto(1d, 2d),
        new ArrayList<>());

    Mockito.when(searchResultMapper.mapSearchResultToOutputDto(getExampleSearchResult()))
        .thenReturn(searchResultDto);

    String resultJson = mvc.perform(get("/v2/locations/search/Mannheim")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    String compareJson = new ObjectMapper().writeValueAsString(searchResultDto);
    assertThat(compareJson, equalTo(resultJson));
  }

  @SneakyThrows
  @Test
  void getLocationTypesTest() {
    List<String> locationTypes = new ArrayList<>();
    locationTypes.add("testType1");
    locationTypes.add("testType2");

    Mockito.when(locationService.getAllLocationTypes()).thenReturn(locationTypes);

    String resultJson = mvc.perform(get("/v2/locations/types")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    String compareJson = new ObjectMapper().writeValueAsString(locationTypes);
    assertThat(compareJson, equalTo(resultJson));
  }
}