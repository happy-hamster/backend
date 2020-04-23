package de.sakpaas.backend.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import de.sakpaas.backend.HappyHamsterTest;
import de.sakpaas.backend.dto.OsmResultLocationListDto;
import de.sakpaas.backend.util.OsmImportConfiguration;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;


@SpringBootTest
@RunWith(SpringRunner.class)
class LocationApiSearchDasTest extends HappyHamsterTest {

  final String country = "DE";
  @Autowired
  LocationApiSearchDas locationApiSearchDas;
  @Autowired
  private RestTemplate restTemplate;

  @SneakyThrows
  @Test
  void makeRequest() {

    OsmImportConfiguration importConfiguration = new OsmImportConfiguration();
    importConfiguration.setCountry(country);
    importConfiguration.setShoptypes(Arrays.asList("type1", "type2"));

    LocationApiSearchDas mockedLocationApiSearchDas = Mockito.spy(locationApiSearchDas);
    Mockito.doReturn("/test").when(mockedLocationApiSearchDas).queryUrlBuilder(Mockito.any());

    MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);

    String responseBody = "{\n" +
        "  \"version\": 0.6,\n" +
        "  \"generator\": \"Overpass API 0.7.56.1002 b121d216\",\n" +
        "  \"osm3s\": {\n" +
        "    \"timestamp_osm_base\": \"2020-04-22T19:51:02Z\",\n" +
        "    \"timestamp_areas_base\": \"2020-03-06T11:03:01Z\",\n" +
        "    \"copyright\": \"The data included in this document is from www.openstreetmap.org. The data is made available under ODbL.\"\n" +
        "  },\n" +
        "  \"elements\": [\n" +
        "\n" +
        "{\n" +
        "  \"type\": \"node\",\n" +
        "  \"id\": 26010622,\n" +
        "  \"lat\": 53.5329648,\n" +
        "  \"lon\": 9.8793023,\n" +
        "  \"tags\": {\n" +
        "    \"addr:city\": \"Hamburg\",\n" +
        "    \"addr:country\": \"DE\",\n" +
        "    \"addr:housenumber\": \"43\",\n" +
        "    \"addr:postcode\": \"21129\",\n" +
        "    \"addr:street\": \"Steendiek\",\n" +
        "    \"brand\": \"Budnikowsky\",\n" +
        "    \"brand:wikidata\": \"Q1001516\",\n" +
        "    \"brand:wikipedia\": \"de:Budnikowsky\",\n" +
        "    \"name\": \"Budnikowsky\",\n" +
        "    \"opening_hours\": \"Mo-Fr 08:00-20:00; Sa 08:00-18:00\",\n" +
        "    \"shop\": \"chemist\",\n" +
        "    \"website\": \"https://www.budni.de\",\n" +
        "    \"wheelchair\": \"limited\"\n" +
        "  }\n" +
        "}]}";
    mockServer.expect(ExpectedCount.once(), requestTo(new URI("/test")))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withStatus(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(responseBody));

    List<OsmResultLocationListDto.OsmResultLocationDto> resultLocationDtos =
        mockedLocationApiSearchDas.getLocationsForCountry(importConfiguration);

    assertThat(resultLocationDtos.size()).as("Wrong size").isEqualTo(1);
    assertThat(resultLocationDtos.get(0).getCoordinates().getLat()).as("Wrong Coordinates")
        .isEqualTo(53.5329648);
    assertThat(resultLocationDtos.get(0).getBrand()).as("Wrong brand").isEqualTo("Budnikowsky");
    assertThat(resultLocationDtos.get(0).getId()).as("Wrong id").isEqualTo(26010622);

  }

  @Test
  void testQueryStringBuilder() {
    OsmImportConfiguration importConfiguration = new OsmImportConfiguration();
    importConfiguration.setCountry(country);
    importConfiguration.setShoptypes(Arrays.asList("type1", "type2"));
    String url = locationApiSearchDas.queryUrlBuilder(importConfiguration);

    assertThat(url)
        .isEqualTo("https://overpass-api.de/api/interpreter?data=[out:json][timeout:2500];" +
            "area[\"ISO3166-1:alpha2\"=DE]->.searchArea;(node[shop=type1](area.searchArea);" +
            "way[shop=type1](area.searchArea);node[shop=type2](area.searchArea);" +
            "way[shop=type2](area.searchArea););out center;");
  }
  
}