package de.sakpaas.backend.dto;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static java.util.Collections.emptyList;

@Component
public class LocationApiSearchDAS {

    public List<LocationSearchOSMResultDto> getLocationByCoordinates(Double latitude, Double longitude,
            Double radius) {

        final String url = "https://overpass-api.de/api/interpreter?data=[out:json];node[shop](around:" + radius
                .toString() + "," + latitude.toString() + "," + longitude.toString() + ");out;way[shop](around:" + radius
                .toString() + "," + latitude.toString() + "," + longitude.toString() + ");out center;";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ApiResultDto> response = restTemplate.getForEntity(url, ApiResultDto.class);

        if (response.getBody() == null) {
            return emptyList();
        }

        return response.getBody().getElements();
    }

    public LocationSearchOSMResultDto getLocationById(Long id) {
        final String url = "https://overpass-api.de/api/interpreter?data=[out:json];node(" + id + ");out;";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ApiResultDto> response = restTemplate.getForEntity(url, ApiResultDto.class);

        if (response.getBody() == null) {
            return null;
        }

        return response.getBody().getElements().get(0);
    }
}
