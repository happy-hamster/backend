package de.sakpaas.backend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

@Getter
@JsonPropertyOrder({ "lat", "lon" })
public class CenterDto {
    private double lat;
    private double lon;

    @JsonCreator
    public CenterDto(@JsonProperty("lat") double lat,
            @JsonProperty("lon") double lon) {
        this.lat = lat;
        this.lon = lon;
    }
}
