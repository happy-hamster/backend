package de.sakpaas.backend.model;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccumulatedOccupancy {

  private Double value;
  private Integer count;
  private ZonedDateTime latestReport;
}
