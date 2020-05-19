package de.sakpaas.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Data
public class CoordinateDetails {
  private final Double latitude;
  private final Double longitude;
}
