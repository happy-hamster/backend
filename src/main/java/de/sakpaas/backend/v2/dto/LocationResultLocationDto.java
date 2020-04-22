package de.sakpaas.backend.v2.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.sakpaas.backend.model.AccumulatedOccupancy;
import de.sakpaas.backend.model.Address;
import de.sakpaas.backend.model.LocationDetails;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@JsonPropertyOrder({"id", "name", "favorite", "details", "coordinates", "occupancy", "address"})
public class LocationResultLocationDto {

  private final long id;
  private final String name;
  private final boolean favorite;
  private final LocationResultLocationDetailsDto details;
  private final LocationResultCoordinatesDto coordinates;
  private final LocationResultOccupancyDto occupancy;
  private final LocationResultAddressDto address;

  /**
   * Creates a v2 LocationResultLocationDto from JSON.
   *
   * @param id          the id
   * @param name        the name
   * @param favorite    flag if Location is Favorite of user
   * @param details     the LocationResultLocationDetailsDto
   * @param coordinates the LocationResultCoordinatesDto
   * @param occupancy   the LocationResultOccupancyDto
   * @param address     the LocationResultAddressDto
   */
  @JsonCreator
  public LocationResultLocationDto(@JsonProperty("id") long id,
                                   @JsonProperty("name") String name,
                                   @JsonProperty("favorite") boolean favorite,
                                   @JsonProperty("details")
                                         LocationResultLocationDetailsDto details,
                                   @JsonProperty("coordinates")
                                         LocationResultCoordinatesDto coordinates,
                                   @JsonProperty("occupancy") LocationResultOccupancyDto occupancy,
                                   @JsonProperty("address") LocationResultAddressDto address) {
    this.id = id;
    this.name = (name != null) ? name : "Supermarkt";
    this.favorite = favorite;
    this.details = details;
    this.coordinates = coordinates;
    this.occupancy = occupancy;
    this.address = address;
  }

  @Data
  public static class LocationResultLocationDetailsDto {

    private String type;
    private String openingHours;
    private String brand;

    /**
     * Creates a v2 {@link LocationResultCoordinatesDto} from a {@link LocationDetails}.
     *
     * @param locationDetails the {@link LocationDetails} to be referenced
     */
    public LocationResultLocationDetailsDto(LocationDetails locationDetails) {
      this.type = locationDetails.getType();
      this.openingHours = locationDetails.getOpeningHours();
      this.brand = locationDetails.getBrand();
    }
  }

  @Data
  public static class LocationResultAddressDto {

    private String country;
    private String city;
    private String postcode;
    private String street;
    private String housenumber;

    /**
     * Creates a v2 {@link LocationResultAddressDto} from an {@link Address}.
     *
     * @param address the {@link Address} to be referenced
     */
    public LocationResultAddressDto(Address address) {
      this.country = address.getCountry();
      this.city = address.getCity();
      this.postcode = address.getPostcode();
      this.street = address.getStreet();
      this.housenumber = address.getHousenumber();
    }
  }

  @Data
  @AllArgsConstructor
  public static class LocationResultCoordinatesDto {
    private double latitude;
    private double longitude;
  }

  @Data
  @AllArgsConstructor
  public static class LocationResultOccupancyDto {

    private Double value;
    private Integer count;
    private ZonedDateTime latestReport;

    /**
     * Creates a v2 {@link LocationResultOccupancyDto} from a {@link AccumulatedOccupancy}.
     *
     * @param accumulatedOccupancy the {@link AccumulatedOccupancy} to be referenced
     */
    public LocationResultOccupancyDto(AccumulatedOccupancy accumulatedOccupancy) {
      this.value = accumulatedOccupancy.getValue();
      this.count = accumulatedOccupancy.getCount();
      this.latestReport = accumulatedOccupancy.getLatestReport();
    }
  }
}
