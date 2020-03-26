package de.sakpaas.backend.dto.locationresult;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"value", "count", "lastestReport"})
public class LocationResultOccupancyDto {

    private Double value = null;
    private Integer count = null;
    private ZonedDateTime lastestReport = null;

}
