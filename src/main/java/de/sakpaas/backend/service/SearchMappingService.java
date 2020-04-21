package de.sakpaas.backend.service;

import com.google.common.annotations.VisibleForTesting;
import de.sakpaas.backend.dto.NominatimSearchResultListDto;
import de.sakpaas.backend.dto.NominatimSearchResultListDto.NominatimResultLocationDto;
import de.sakpaas.backend.model.CoordinateDetails;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Service
public class SearchMappingService {
  private static final Logger LOGGER = LoggerFactory.getLogger(SearchMappingService.class);

  @Value("${app.search-api-url}")
  private String searchApiUrl;

  /**
   * Searches in the Nominatim Microservice for the given key.
   *
   * @param key The search parameter. Multiple words are separated with %20.
   * @return The list of Locations in our database
   */
  public CoordinateDetails search(String key) {
    // Makes a request to the Nominatim Microservice
    String url = this.searchApiUrl + "/search/" + key + "?format=json";
    LOGGER.info("URL: " + url);

    final List<NominatimResultLocationDto> list = makeRequest(url);
    LOGGER.info("Liste: " + list);
    return calculateCenter(list);
  }

  /**
   * Makes an REST request to the provided URL.
   *
   * @param url The URL the request points to
   * @return A List of NominatimResultLocationDto
   */
  @VisibleForTesting
  protected List<NominatimResultLocationDto> makeRequest(String url) {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.set(HttpHeaders.ACCEPT, "text/html");
    HttpEntity<String> entityReq = new HttpEntity<>(httpHeaders);
    ResponseEntity<NominatimSearchResultListDto> response =
        restTemplate.exchange(url, HttpMethod.GET, entityReq, NominatimSearchResultListDto.class);
    if (response.getBody() == null) {
      return new ArrayList<>();
    }

    return response.getBody().getElements();
  }

  /**
   * Calculates the central coordinates of a list of NominatimResultLocationDto.
   *
   * @param list The list of NominatimResultLocationDto
   * @return The central coordinates
   */
  @VisibleForTesting
  protected CoordinateDetails calculateCenter(List<NominatimResultLocationDto> list) {

    double latitude = list.stream()
        .map(NominatimResultLocationDto::getLat)
        .mapToDouble(lat -> lat)
        .average().orElse(0.0);
    double longitude = list.stream()
        .map(NominatimResultLocationDto::getLon)
        .mapToDouble(lon -> lon)
        .average().orElse(0.0);

    return new CoordinateDetails(latitude, longitude);
  }
}
