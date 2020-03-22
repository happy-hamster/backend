package de.sakpaas.backend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@JsonPropertyOrder({"locationId", "occupancy", "clientType"})
@Getter
@Setter
public class OccupancyDto {
    private Long locationId;
    private Double occupancy;
    private String clientType;

    @JsonCreator
    public OccupancyDto(@JsonProperty("locationId") Long locationId,
                        @JsonProperty("occupancy") Double occupancy, @JsonProperty("clientType") String clientType) {
        this.locationId = locationId;
        this.occupancy = occupancy;
        this.clientType = clientType;
    }
}
