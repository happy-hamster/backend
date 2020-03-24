package de.sakpaas.backend.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.sakpaas.backend.model.LocationDetails;
import lombok.Data;

@Data
@JsonPropertyOrder({ "type", "openingHours" })
public class LocationDetailsDto {

  private String type;
  private String openingHours;

  public LocationDetailsDto(LocationDetails locationDetails) {
    this.type = locationDetails.getType();
    this.openingHours = locationDetails.getOpeningHours();
  }

}
