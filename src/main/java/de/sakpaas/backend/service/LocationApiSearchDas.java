package de.sakpaas.backend.service;

import static java.util.Collections.emptyList;

import de.sakpaas.backend.dto.OsmResultLocationListDto;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class LocationApiSearchDas {

  /**
   * Gets all Locations in a Country (currently only Germany).
   *
   * @param countryCode currently Dummy as lookup for area id is missing
   * @return list of supermarkets in Country
   */
  public List<OsmResultLocationListDto.OsmResultLocationDto> getLocationsForCountry(
      String countryCode) {
    final String url =
        "https://overpass-api.de/api/interpreter?data=[out:json][timeout:2500];area(3600051477)->.searchArea;("
            + "node[shop=supermarket](area.searchArea);way[shop=supermarket](area.searchArea);"
            + "node[shop=chemist](area.searchArea);way[shop=chemist](area.searchArea);"
            + "node[shop=beverages](area.searchArea);way[shop=beverages](area.searchArea);"
            + ");out center;";
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<OsmResultLocationListDto> response =
        restTemplate.getForEntity(url, OsmResultLocationListDto.class);

    if (response.getBody() == null) {
      return emptyList();
    }

    return response.getBody().getElements();
  }
}
