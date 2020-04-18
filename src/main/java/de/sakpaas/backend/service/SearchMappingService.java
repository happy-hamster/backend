package de.sakpaas.backend.service;


import com.google.common.annotations.VisibleForTesting;
import de.sakpaas.backend.dto.NominatimSearchResultListDto;
import de.sakpaas.backend.dto.NominatimSearchResultListDto.NominatimResultLocationDto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SearchMappingService {

  @Value("@{app.search-api-url}")
  private String searchApiUrl;

  /**
   * Searches in the Nominatim Microservice for the given key.
   *
   * @param key The search parameter. Multiple words are separated with %20.
   * @return The list of Locations in our database
   */
  public Map<String, Double> search(String key) {
    // Makes a request to the Nominatim Microservice
    final String url = this.searchApiUrl + "/search/" + key + "?format=json";
    final List<NominatimResultLocationDto> list = makeRequest(url);
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
    ResponseEntity<NominatimSearchResultListDto> response =
        restTemplate.getForEntity(url, NominatimSearchResultListDto.class);

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
  protected Map<String, Double> calculateCenter(List<NominatimResultLocationDto> list) {
    List<Double> lat = new ArrayList<>();
    List<Double> lon = new ArrayList<>();
    for (NominatimResultLocationDto element : list) {
      lat.add(element.getLat());
      lon.add(element.getLon());
    }
    final Map<String, Double> map = new HashMap<>();
    map.put("lat", calculateAvg(lat));
    map.put("lon", calculateAvg(lon));
    return map;
  }

  /**
   * Calculates the average of the provided doubles.
   *
   * @param list The list of doubles the average has to be calculated for
   * @return The average of the provided doubles
   */
  @VisibleForTesting
  protected double calculateAvg(List<Double> list) {
    double result = list.stream().mapToDouble(f -> f).sum();
    return result / list.size();
  }
}
