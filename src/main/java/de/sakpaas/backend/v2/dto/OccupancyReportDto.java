package de.sakpaas.backend.v2.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({"locationId", "occupancy", "clientType"})
public class OccupancyReportDto {

    private Long locationId;
    private Double occupancy;
    private String clientType;

    @JsonCreator
    public OccupancyReportDto(@JsonProperty("locationId") Long locationId,
                              @JsonProperty("occupancy") Double occupancy,
                              @JsonProperty("clientType") String clientType) {
        this.locationId = locationId;
        this.occupancy = occupancy;
        this.clientType = clientType;
    }

}
