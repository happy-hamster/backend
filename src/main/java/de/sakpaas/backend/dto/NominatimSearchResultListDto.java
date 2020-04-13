package de.sakpaas.backend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

@Getter
public class NominatimSearchResultListDto {

  private List<NominatimResultLocationDto> elements;

  @JsonCreator
  public NominatimSearchResultListDto(List<NominatimResultLocationDto> elements) {
    this.elements = elements;
  }

  @Getter
  public static class NominatimResultLocationDto {

    private long osmId;

    @JsonCreator
    public NominatimResultLocationDto(
        @JsonProperty("osm_id") long osmId
    ) {
      this.osmId = osmId;
    }

    public long getOsmId() {
      return osmId;
    }
  }
}
