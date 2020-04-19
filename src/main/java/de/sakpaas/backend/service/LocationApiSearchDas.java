package de.sakpaas.backend.service;

import static java.util.Collections.emptyList;

import de.sakpaas.backend.dto.OsmResultLocationListDto;
import de.sakpaas.backend.util.ShoptypeListConfig;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class LocationApiSearchDas {


  /**
   * Gets all Locations in a Country (currently only Germany).
   *
   * @param countryCode ISO3166-1:alpha2 Code
   * @return list of supermarkets in Country
   */
  public List<OsmResultLocationListDto.OsmResultLocationDto> getLocationsForCountry(
      String countryCode, ShoptypeListConfig shoptypeListConfig) {
    // TODO: lookup of areaId by countryCode from overpass-api (TINF-70)
    StringBuilder url =
        new StringBuilder("https://overpass-api.de/api/interpreter?data=[out:json][timeout:2500];"
            + "area[\"ISO3166-1:alpha2\"=" + countryCode + "]->.searchArea;(");

    // Add shoptypes from configuration
    for (String shoptype : shoptypeListConfig.getShoptypes()) {
      url.append("node[shop=").append(shoptype).append("](area.searchArea);way[shop=")
          .append(shoptype).append("](area.searchArea);");
    }

    url.append(");out center;");


    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<OsmResultLocationListDto> response =
        restTemplate.getForEntity(url.toString(), OsmResultLocationListDto.class);

    if (response.getBody() == null) {
      return emptyList();
    }

    return response.getBody().getElements();
  }
}
