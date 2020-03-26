package de.sakpaas.backend.service;

import de.sakpaas.backend.dto.osmresult.OMSResultLocationDto;
import de.sakpaas.backend.dto.osmresult.OSMResultLocationListDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static java.util.Collections.emptyList;

@Component
public class LocationApiSearchDAS {

    public List<OMSResultLocationDto> getLocationByCoordinates(Double latitude, Double longitude,
                                                               Double radius) {

        final String url = "https://overpass-api.de/api/interpreter?data=[out:json];" +
                "node[shop=supermarket](around:" + radius.toString() + "," + latitude.toString() + "," + longitude.toString() + ");out;" +
                "way[shop=supermarket](around:" + radius.toString() + "," + latitude.toString() + "," + longitude.toString() + ");out center;" +
                "node[shop=chemist](around:" + radius.toString() + "," + latitude.toString() + "," + longitude.toString() + ");out;" +
                "way[shop=chemist](around:" + radius.toString() + "," + latitude.toString() + "," + longitude.toString() + ");out center;";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<OSMResultLocationListDto> response = restTemplate.getForEntity(url, OSMResultLocationListDto.class);

        if (response.getBody() == null) {
            return emptyList();
        }

        return response.getBody().getElements();
    }

    /**
     * gets all Locations in a Country (currently only Germany)
     *
     * @param countryCode currently Dummy as lookup for area id is missing
     * @return list of supermarkets in Country
     */
    public List<OMSResultLocationDto> getLocationsForCountry(String countryCode) {
        final String url = "https://overpass-api.de/api/interpreter?data=[out:json][timeout:2500];area(3600051477)->." +
                "searchArea;(node[shop=supermarket](area.searchArea);way[shop=supermarket](area.searchArea);" +
                "node[shop=chemist](area.searchArea);way[shop=chemist](area.searchArea););out center;";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<OSMResultLocationListDto> response = restTemplate.getForEntity(url, OSMResultLocationListDto.class);

        if (response.getBody() == null) {
            return emptyList();
        }

        return response.getBody().getElements();
    }

    public OMSResultLocationDto getLocationById(Long id) {
        final String url = "https://overpass-api.de/api/interpreter?data=[out:json];node(" + id + ");out;";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<OSMResultLocationListDto> response = restTemplate.getForEntity(url, OSMResultLocationListDto.class);

        if (response.getBody() == null) {
            return null;
        }

        return response.getBody().getElements().get(0);
    }
}
