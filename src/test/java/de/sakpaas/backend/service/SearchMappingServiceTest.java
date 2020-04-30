package de.sakpaas.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import de.sakpaas.backend.HappyHamsterTest;
import de.sakpaas.backend.dto.NominatimSearchResultListDto;
import de.sakpaas.backend.model.CoordinateDetails;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
class SearchMappingServiceTest extends HappyHamsterTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(SearchMappingServiceTest.class);
  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private SearchMappingService searchMappingService;


  @Test
  void searchWithoutCoordinates() {
    SearchMappingService mockService = Mockito.spy(searchMappingService);
    NominatimSearchResultListDto mockedList =
        new NominatimSearchResultListDto(new ArrayList<>(
            Collections
                .singletonList(new NominatimSearchResultListDto.NominatimResultLocationDto(3, 5))));
    Mockito.doReturn(mockedList).when(mockService)
        .makeRequest();

    final CoordinateDetails result = mockService.search(Collections.singleton("München"));
    assertEquals(new CoordinateDetails(3.0, 5.0), result);
  }

  @Test
  void searchWithCoordinates() {
    SearchMappingService mockService = Mockito.spy(searchMappingService);
    NominatimSearchResultListDto mockedList =
        new NominatimSearchResultListDto(new ArrayList<>(
            Collections
                .singletonList(new NominatimSearchResultListDto.NominatimResultLocationDto(3, 5))));
    Mockito.doReturn(mockedList).when(mockService)
        .makeRequest();

    final CoordinateDetails result =
        mockService.search(Collections.singleton("München"), new CoordinateDetails(1.0, 1.0));
    assertEquals(new CoordinateDetails(3.0, 5.0), result);
  }

  @SneakyThrows
  @Test
  void makeRequest() {
    MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);

    String responseBody =
        "[{\"place_id\":97754986,\"licence\":\"Data © OpenStreetMap contributors, ODbL 1.0. https://osm.org/copyright\",\"osm_type\":\"way\",\"osm_id\":45186996,\"boundingbox\":[\"49.3188712\",\"49.3191267\",\"9.3630305\",\"9.3637289\"],\"lat\":\"49.3190277\",\"lon\":\"9.363421444681375\",\"display_name\":\"Lidl, 10, Daimlerstraße, Schillerhöhe, Möckmühl, Verwaltungsgemeinschaft Möckmühl, Landkreis Heilbronn, Baden-Württemberg, 74219, Germany\",\"class\":\"shop\",\"type\":\"supermarket\",\"importance\":0.22100000000000003,\"icon\":\"https://nominatim.openstreetmap.org/images/mapicons/shopping_supermarket.p.20.png\"}]";
    mockServer.expect(ExpectedCount.once(), requestTo(new URI("/test.de")))
        .andExpect(method(HttpMethod.GET))
        .andExpect(header(HttpHeaders.ACCEPT, "text/html"))
        .andRespond(withStatus(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(responseBody));

    searchMappingService.url = "test.de";

    NominatimSearchResultListDto nominatimSearchResultListDto =
        searchMappingService.makeRequest();
    assertThat(nominatimSearchResultListDto.getElements().size()).isEqualTo(1);
    assertThat(nominatimSearchResultListDto.getElements().get(0).getLat()).isEqualTo(49.3190277);
    assertThat(nominatimSearchResultListDto.getElements().get(0).getLon())
        .isEqualTo(9.363421444681375);
  }
}