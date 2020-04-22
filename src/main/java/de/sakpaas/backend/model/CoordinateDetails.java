package de.sakpaas.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Data
public class CoordinateDetails {
  private final double latitude;
  private final double longitude;
}
