package de.sakpaas.backend.service;

import com.google.common.annotations.VisibleForTesting;
import de.sakpaas.backend.dto.NominatimSearchResultListDto;
import de.sakpaas.backend.model.CoordinateDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SearchMappingService {
  private static final Logger LOGGER = LoggerFactory.getLogger(SearchMappingService.class);

  private final RestTemplate restTemplate = new RestTemplate();

  @Value("${app.search-api-url}")
  private String searchApiUrl;

  /**
   * Searches in the Nominatim Microservice for the given key.
   *
   * @param key The search parameter. Multiple words are separated with %20.
   * @return The list of Locations in our database
   */
  public CoordinateDetails search(String key) {
    String url = this.searchApiUrl + "/search/" + key + "?format=json";
    LOGGER.info("URL: " + url);

    final NominatimSearchResultListDto list = makeRequest(url);
    LOGGER.info("Liste: " + list);
    return new CoordinateDetails(list.getElements().get(0).getLat(),
        list.getElements().get(0).getLon());
  }

  /**
   * Makes an REST request to the provided URL.
   *
   * @param url The URL the request points to
   * @return A List of NominatimResultLocationDto
   */
  @VisibleForTesting
  protected NominatimSearchResultListDto makeRequest(String url) {
    HttpEntity<String> header = setupRequest();
    ResponseEntity<NominatimSearchResultListDto> response =
        restTemplate
            .exchange(url, HttpMethod.GET, header, NominatimSearchResultListDto.class);

    return response.getBody();
  }

  /**
   * Sets the HttpHeader for the request.
   *
   * @return The HttpHeader
   */
  private HttpEntity<String> setupRequest() {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.set(HttpHeaders.ACCEPT, "text/html");
    return new HttpEntity<>(httpHeaders);
  }
}
