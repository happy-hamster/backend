package de.sakpaas.backend.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonPropertyOrder({ "latitude", "longitude" })
public class CoordinatesDto {

  private double latitude;
  private double longitude;

}
