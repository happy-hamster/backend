package de.sakpaas.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
public class AccumulatedOccupancy {

    private Double value;
    private Integer count;
    private ZonedDateTime latestReport;

}
