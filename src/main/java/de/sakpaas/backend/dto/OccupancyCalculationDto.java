package de.sakpaas.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OccupancyCalculationDto {

  private Double value = null;
  private Integer count = null;

}
