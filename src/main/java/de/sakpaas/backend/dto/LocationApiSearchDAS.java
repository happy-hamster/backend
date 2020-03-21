package de.sakpaas.backend.dto;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Repository("LocationApiSearchDAS")
public class LocationApiSearchDAS {

    WebClient client = WebClient.create("https://overpass-api.de/api/interpreter");

    public ResponseEntity<List<LocationSearchOSMResultDto>> getLocationByCoordinates(Double latitude, Double longitude,
            Double radius) {
        return client.get()
                .uri("?data=[out:json];node[shop=supermarket](around:" + radius.toString() + "," + latitude.toString() + "," + longitude
                        .toString() + ");out;")
                .retrieve()
                .toEntityList(LocationSearchOSMResultDto.class)
                .block();

    }
}
