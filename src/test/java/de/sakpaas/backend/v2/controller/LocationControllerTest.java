package de.sakpaas.backend.v2.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.sakpaas.backend.HappyHamsterTest;
import de.sakpaas.backend.service.LocationService;
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
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@EnableConfigurationProperties
class LocationControllerTest extends HappyHamsterTest {

  @MockBean
  LocationService locationService;
  @Autowired
  private MockMvc mvc;

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