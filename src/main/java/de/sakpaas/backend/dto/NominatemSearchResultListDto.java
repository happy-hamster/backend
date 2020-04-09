package de.sakpaas.backend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import lombok.Getter;

@JsonPropertyOrder({"elements"})
@Getter
public class NominatemSearchResultListDto {

  private List<NominatemResultLocationDto> elements;

  @JsonCreator
  public NominatemSearchResultListDto(List<NominatemResultLocationDto> elements) {
    this.elements = elements;
  }

  @Getter
  @JsonPropertyOrder({"place_id", "licence", "osm_type", "osm_id", "boundingbox", "lat", "lon",
      "display_name", "class", "type", "importance", "icon"})
  public static class NominatemResultLocationDto {

    private long id;
    private String licence;
    private String osm_type;
    private long osm_id;
    // private ? boundingbox;
    private double lat;
    private double lon;
    private String display_name;
    private String class_name;
    private String type_name;
    private float importance;
    private String icon;

    @JsonCreator
    public NominatemResultLocationDto(
        @JsonProperty("place_id") long id,
        @JsonProperty("licence") String licence,
        @JsonProperty("osm_type") String osm_type,
        @JsonProperty("osm_id") long osm_id,
//        @JsonProperty("boundingbox") ? boundingbox,
        @JsonProperty("lat") double lat,
        @JsonProperty("lon") double lon,
        @JsonProperty("display_name") String display_name,
        @JsonProperty("class") String class_name,
        @JsonProperty("type") String type_name,
        @JsonProperty("importance") float importance,
        @JsonProperty("icon") String icon
    ) {
      this.id = id;
      this.licence = licence;
      this.osm_type = osm_type;
      this.osm_id = osm_id;
//      this.boundingbox = boundingbox;
      this.lat = lat;
      this.lon = lon;
      this.display_name = display_name;
      this.class_name = class_name;
      this.type_name = type_name;
      this.importance = importance;
      this.icon = icon;
    }

    public long getId() {
      return id;
    }

    public String getLicence() {
      return licence;
    }

    public String getOsm_type() {
      return osm_type;
    }

    public long getOsm_id() {
      return osm_id;
    }

    public double getLat() {
      return lat;
    }

    public double getLon() {
      return lon;
    }

    public String getDisplay_name() {
      return display_name;
    }

    public String getClass_name() {
      return class_name;
    }

    public String getType_name() {
      return type_name;
    }

    public float getImportance() {
      return importance;
    }

    public String getIcon() {
      return icon;
    }

    @Override
    public String toString() {
      return "NominatemResultLocationDto{" +
          "id=" + id +
          ", licence='" + licence + '\'' +
          ", osm_type='" + osm_type + '\'' +
          ", osm_id=" + osm_id +
          ", lat='" + lat + '\'' +
          ", lon='" + lon + '\'' +
          ", display_name='" + display_name + '\'' +
          ", class_name='" + class_name + '\'' +
          ", type_name='" + type_name + '\'' +
          ", importance=" + importance +
          ", icon='" + icon + '\'' +
          '}';
    }
  }
}
