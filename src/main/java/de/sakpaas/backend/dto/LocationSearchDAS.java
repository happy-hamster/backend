package de.sakpaas.backend.dto;



import de.sakpaas.backend.model.LocationSearchOSMResultList;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
@Repository("LocationSearchDAS")
public class LocationSearchDAS implements LocationSearchDao {

    WebClient client = WebClient.create("https://overpass-api.de/api/interpreter");

    @Override
    public LocationSearchOSMResultList getLocationByCoordinates(Double latitude, Double longitude, Double radius) {
        return client.get()
                .uri("?data=[out:json];node[shop=supermarket](around:"+radius.toString()+","+latitude.toString()+","+longitude.toString()+");out;")
                .retrieve()
                .bodyToMono(LocationSearchOSMResultList.class)
                .block();

    }
}
