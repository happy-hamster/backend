package de.sakpaas.backend.v2.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.sakpaas.backend.v2.dto.LocationResultLocationDto.LocationResultCoordinatesDto;
import java.util.List;
import lombok.Getter;

@Getter
@JsonPropertyOrder({"coordinates", "locations"})
public class SearchResultDto {

  private final LocationResultCoordinatesDto coordinates;
  private final List<LocationResultLocationDto> locations;

  /**
   * Creates a v2 SearchResultDto from JSON.
   *
   * @param coordinates The coordinates of the central point
   * @param locations   The locations around the coordinates
   */
  @JsonCreator
  public SearchResultDto(
      @JsonProperty("coordinates") LocationResultCoordinatesDto coordinates,
      @JsonProperty("locations") List<LocationResultLocationDto> locations) {
    this.coordinates = coordinates;
    this.locations = locations;
  }
}
