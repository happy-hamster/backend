package de.sakpaas.backend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import lombok.Getter;

@JsonPropertyOrder({"elements"})
@Getter
public class OsmResultLocationListDto {

  private List<OsmResultLocationDto> elements;

  @JsonCreator
  public OsmResultLocationListDto(@JsonProperty("elements") List<OsmResultLocationDto> elements) {
    this.elements = elements;
  }

  @Getter
  @JsonPropertyOrder({"id", "coordinates", "tags"})
  public static class OsmResultLocationDto {

    private long id;
    private OsmResultCoordinatesDto coordinates;
    private OsmResultTagsDto tags;

    /**
     * Creates a {@link OsmResultLocationDto} from JSON.
     *
     * @param id the id
     * @param lat the latitude
     * @param lon the longitude
     * @param center the center location, can be null, the lat and lon will be used
     * @param tags the tags of this OSM Location
     */
    @JsonCreator
    public OsmResultLocationDto(@JsonProperty("id") long id,
                                @JsonProperty("lat") double lat,
                                @JsonProperty("lon") double lon,
                                @JsonProperty("center") OsmResultCoordinatesDto center,
                                @JsonProperty("tags") OsmResultTagsDto tags) {
      this.id = id;
      this.tags = tags;
      this.coordinates = (center != null) ? center : new OsmResultCoordinatesDto(lat, lon);
    }

    public String getName() {
      return tags.getName();
    }

    public String getStreet() {
      return tags.getStreet();
    }

    public String getHousenumber() {
      return tags.getHousenumber();
    }

    public String getPostcode() {
      return tags.getPostcode();
    }

    public String getCity() {
      return tags.getCity();
    }

    public String getCountry() {
      return tags.getCountry();
    }

    public String getType() {
      return tags.getType();
    }

    public String getBrand() {
      return tags.getBrand();
    }

    public String getOpeningHours() {
      return tags.getOpeningHours();
    }

  }

  @Getter
  @JsonPropertyOrder({"lat", "lon"})
  public static class OsmResultCoordinatesDto {
    private double lat;
    private double lon;

    @JsonCreator
    public OsmResultCoordinatesDto(@JsonProperty("lat") double lat,
                                   @JsonProperty("lon") double lon) {
      this.lat = lat;
      this.lon = lon;
    }
  }

  @JsonPropertyOrder({"name", "addr:street", "addr:place", "addr:housenumber", "addr:postcode",
      "addr:city", "addr:country", "shop", "brand", "opening_hours"})
  @Getter
  public static class OsmResultTagsDto {
    private String name;
    private String street;
    private String housenumber;
    private String postcode;
    private String city;
    private String country;
    private String type;
    private String brand;
    private String openingHours;

    /**
     * Creates a {@link OsmResultTagsDto} from JSON.
     *
     * @param name the location name
     * @param street the street of the address
     * @param housenumber the house number of the address
     * @param postcode the post code of the address
     * @param city the city of the address
     * @param country the country of the address
     * @param place currently unused
     * @param type the type of the location (eg. supermarket)
     * @param brand the brand of the location (eg. LIDL, ALDI)
     * @param openingHours the opening hours in human readable format
     */
    @JsonCreator
    public OsmResultTagsDto(@JsonProperty("name") String name,
                            @JsonProperty("addr:street") String street,
                            @JsonProperty("addr:housenumber") String housenumber,
                            @JsonProperty("addr:postcode") String postcode,
                            @JsonProperty("addr:city") String city,
                            @JsonProperty("addr:country") String country,
                            @JsonProperty("addr:place") String place,
                            @JsonProperty("shop") String type,
                            @JsonProperty("brand") String brand,
                            @JsonProperty("opening_hours") String openingHours) {
      this.name = name;
      this.street = street != null ? street : place;
      this.housenumber = housenumber;
      this.postcode = postcode;
      this.city = city;
      this.country = country;
      this.type = type;
      this.brand = brand;
      this.openingHours = openingHours;
    }

  }


}
