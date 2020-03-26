package de.sakpaas.backend.dto.locationresult;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.sakpaas.backend.model.LocationDetails;
import lombok.Data;

@Data
@JsonPropertyOrder({"type", "openingHours"})
public class LocationResultLocationDetailsDto {

  private String type;
  private String openingHours;
  private String brand;

  public LocationResultLocationDetailsDto(LocationDetails locationDetails) {
    this.type = locationDetails.getType();
    this.openingHours = locationDetails.getOpeningHours();
    this.brand = locationDetails.getBrand();
  }

}
