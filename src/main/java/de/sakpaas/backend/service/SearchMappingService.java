package de.sakpaas.backend.service;

import com.google.common.annotations.VisibleForTesting;
import de.sakpaas.backend.dto.NominatimSearchResultListDto;
import de.sakpaas.backend.model.CoordinateDetails;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.Set;
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

  private String url;


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
  }

  /**
   * Searches in the Nominatim Microservice for the given query.
   *
   * @param query The search parameter
   * @return The coordinates of the search request
   * @throws IndexOutOfBoundsException Iff the the request returned nothing
   */
  public CoordinateDetails search(Set<String> query) throws IndexOutOfBoundsException {
    encodeUrl(query);
    return returnCoordinates();
  }

  /**
   * Searches in the Nominatim Microservice for the given query.
   *
   * @param query The search parameter
   * @return The coordinates of the search request
   * @throws IndexOutOfBoundsException Iff the the request returned nothing
   */
  public CoordinateDetails search(Set<String> query, CoordinateDetails coordinateDetails)
      throws IndexOutOfBoundsException {
    encodeUrl(query, coordinateDetails);
    return returnCoordinates();
  }

  /**
   * Encodes the nominatim request query.
   * To be implemented in TINF-272.
   *
   * @param query The search parameter
   */
  private void encodeUrl(Set<String> query) {
    String queryString = String.join(",", query);
    this.url = this.searchApiUrl + "/search/" + queryString + "?format=json&limit=1";
  }

  /**
   * Encodes the nominatim request query.
   * To be implemented in TINF-272.
   *
   * @param query             The search parameter
   * @param coordinateDetails The current coordinates of the user
   */
  private void encodeUrl(Set<String> query, CoordinateDetails coordinateDetails) {
    String queryString = String.join(",", query)
        + "," + coordinateDetails.getLatitude()
        + "," + coordinateDetails.getLongitude();
    this.url = this.searchApiUrl + "/search/" + queryString + "?format=json&limit=1";
  }

  /**
   * Helper method that merges the overloaded search methods and makes
   * the request and returns coordinates.
   *
   * @return The coordinates of the search request
   * @throws IndexOutOfBoundsException Iff the the request returned nothing
   */
  private CoordinateDetails returnCoordinates() throws IndexOutOfBoundsException {
    final NominatimSearchResultListDto list = makeRequest();

    return new CoordinateDetails(list.getElements().get(0).getLat(),
        list.getElements().get(0).getLon());
  }


  /**
   * Makes an REST request to the provided URL.
   *
   * @return A List of NominatimResultLocationDto
   */
  @VisibleForTesting
  protected NominatimSearchResultListDto makeRequest() {

    Timer timer = Timer
        .builder("nominatim.request")
        .description("Times the duration of the Nominatim search requests")
        .register(this.meterRegistry);


    StopWatch watch = new StopWatch();
    HttpEntity<String> header = setupRequest();
    watch.start();
    ResponseEntity<NominatimSearchResultListDto> response =
        this.restTemplate
            .exchange(this.url, HttpMethod.GET, header, NominatimSearchResultListDto.class);
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
