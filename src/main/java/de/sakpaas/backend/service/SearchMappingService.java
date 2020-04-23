package de.sakpaas.backend.service;

import com.google.common.annotations.VisibleForTesting;
import de.sakpaas.backend.dto.NominatimSearchResultListDto;
import de.sakpaas.backend.exception.NoSearchResultsException;
import de.sakpaas.backend.model.CoordinateDetails;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

@Service
public class SearchMappingService {
  private static final Logger LOGGER = LoggerFactory.getLogger(SearchMappingService.class);

  private final RestTemplate restTemplate;
  private final MeterRegistry meterRegistry;

  private Timer timer;

  @Value("${app.search-api-url}")
  private String searchApiUrl;

  /**
   * Default Constructor.
   *
   * @param restTemplate  The RestTemplate
   * @param meterRegistry The MeterRegistry
   */
  public SearchMappingService(RestTemplate restTemplate,
                              MeterRegistry meterRegistry) {
    this.restTemplate = restTemplate;
    this.meterRegistry = meterRegistry;

    timer = Timer
        .builder("nominatim.request")
        .description("Times the duration of the Nominatim search requests")
        .register(this.meterRegistry);

  }

  /**
   * Searches in the Nominatim Microservice for the given key.
   *
   * @param key The search parameter. Multiple words are separated with %20.
   * @return The list of Locations in our database
   */
  public CoordinateDetails search(String key) {
    String url = this.searchApiUrl + "/search/" + key + "?format=json";

    final NominatimSearchResultListDto list = makeRequest(url);

    try {
      return new CoordinateDetails(list.getElements().get(0).getLat(),
          list.getElements().get(0).getLon());
    } catch (IndexOutOfBoundsException e) {
      throw new NoSearchResultsException(
          "Under the URL (" + url + ") no coordinates could be calculated", url);
    }
  }

  /**
   * Makes an REST request to the provided URL.
   *
   * @param url The URL the request points to
   * @return A List of NominatimResultLocationDto
   */
  @VisibleForTesting
  protected NominatimSearchResultListDto makeRequest(String url) {
    StopWatch watch = new StopWatch();
    HttpEntity<String> header = setupRequest();
    watch.start();
    ResponseEntity<NominatimSearchResultListDto> response =
        restTemplate
            .exchange(url, HttpMethod.GET, header, NominatimSearchResultListDto.class);
    watch.stop();
    timer.record(watch.getLastTaskTimeMillis(), TimeUnit.MILLISECONDS);

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
