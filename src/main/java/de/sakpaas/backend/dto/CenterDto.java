package de.sakpaas.backend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

@Getter
@JsonPropertyOrder({ "lat", "lon" })
public class CenterDto {
    private Long lat;
    private Long lon;

    @JsonCreator
    public CenterDto(@JsonProperty("lat") Long lat,
            @JsonProperty("lon") Long lon) {
        this.lat = lat;
        this.lon = lon;
    }
}
