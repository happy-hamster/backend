package de.sakpaas.backend.v1.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonPropertyOrder({"locationId", "occupancy", "clientType"})
public class OccupancyReportDto {

  private Long locationId;

  @NotNull(message = "'occupancy' can not be null.")
  @DecimalMin(value = "0.0", message = "'occupancy' has to be between 0.0 and 1.0.")
  @DecimalMax(value = "1.0", message = "'occupancy' has to be between 0.0 and 1.0.")
  private Double occupancy;

  private String clientType;

  /**
   * Creates a new v1 {@link OccupancyReportDto} from JSON.
   *
   * @param locationId the location id
   * @param occupancy the occupancy (from 0.0 to 1.0)
   * @param clientType the client type (eg. IOT, WEB_CLIENT)
   */
  @JsonCreator
  public OccupancyReportDto(@JsonProperty("locationId") Long locationId,
                            @JsonProperty("occupancy") Double occupancy,
                            @JsonProperty("clientType") String clientType) {
    this.locationId = locationId;
    this.occupancy = occupancy;
    this.clientType = clientType;
  }

}
