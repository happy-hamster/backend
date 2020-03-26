package de.sakpaas.backend.dto.locationresult;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonPropertyOrder({"latitude", "longitude"})
public class LocationResultCoordinatesDto {

  private double latitude;
  private double longitude;

}
