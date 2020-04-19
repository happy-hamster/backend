package de.sakpaas.backend.service;

import static java.util.Collections.emptyList;

import de.sakpaas.backend.dto.OsmResultLocationListDto;
import de.sakpaas.backend.util.OsmImportConfiguration;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class LocationApiSearchDas {


  /**
   * Gets all Locations of specific types in a specific Country.
   *
   * @param osmImportConfiguration Object which contains all information about the data to load
   * @return list of supermarkets in Country
   */
  public List<OsmResultLocationListDto.OsmResultLocationDto> getLocationsForCountry(

      OsmImportConfiguration osmImportConfiguration) {
    // TODO: lookup of areaId by countryCode from overpass-api (TINF-70)

    // If no location types specified, there is nothing to load
    if (osmImportConfiguration.getShoptypes().size() == 0) {
      return emptyList();
    }

    // build request string
    StringBuilder url =
        new StringBuilder("https://overpass-api.de/api/interpreter?data=[out:json][timeout:2500];")
            .append("area[\"ISO3166-1:alpha2\"=").append(osmImportConfiguration.getCountry())
            .append("]->.searchArea;(");

    // Add shoptypes from configuration
    for (String shoptype : osmImportConfiguration.getShoptypes()) {
      url.append("node[shop=").append(shoptype).append("](area.searchArea);way[shop=")
          .append(shoptype).append("](area.searchArea);");
    }

    url.append(");out center;");

    // make request

    
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<OsmResultLocationListDto> response =
        restTemplate.getForEntity(url.toString(), OsmResultLocationListDto.class);

    if (response.getBody() == null) {
      return emptyList();
    }

    return response.getBody().getElements();
  }

}
