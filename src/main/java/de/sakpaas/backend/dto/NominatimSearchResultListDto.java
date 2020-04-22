package de.sakpaas.backend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

@Getter
public class NominatimSearchResultListDto {

  private final List<NominatimResultLocationDto> elements;

  /**
   * Creates a {@link NominatimSearchResultListDto} from JSON.
   *
   * @param elements The elements in the search response array
   */
  @JsonCreator
  public NominatimSearchResultListDto(List<NominatimResultLocationDto> elements) {
    this.elements = elements;
  }

  @Override
  public String toString() {
    return "NominatimSearchResultListDto{" + "elements=" + elements + '}';
  }

  @Getter
  public static class NominatimResultLocationDto {

    private final double lat;
    private final double lon;

    /**
     * Default constructor.
     *
     * @param lat The latitude of the locations coordinates
     * @param lon The longitude of the locations coordinates
     */
    @JsonCreator
    public NominatimResultLocationDto(
        @JsonProperty("lat") double lat,
        @JsonProperty("lon") double lon
    ) {
      this.lat = lat;
      this.lon = lon;
    }

    @Override
    public String toString() {
      return "NominatimResultLocationDto{" + "lat=" + lat + ", lon=" + lon + '}';
    }
  }
}
