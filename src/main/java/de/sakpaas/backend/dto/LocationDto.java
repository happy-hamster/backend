package de.sakpaas.backend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

@Getter
@JsonPropertyOrder({"id", "name", "details", "coordinates", "occupancy", "address"})
public class LocationDto {

    private long id;
    private String name;
    private LocationDetailsDto details;
    private CoordinatesDto coordinates;
    private OccupancyCalculationDto occupancy;
    private AddressDto address;

    @JsonCreator
    public LocationDto(@JsonProperty("id") long id,
                       @JsonProperty("name") String name,
                       @JsonProperty("details") LocationDetailsDto details,
                       @JsonProperty("coordinates") CoordinatesDto coordinates,
                       @JsonProperty("occupancy") OccupancyCalculationDto occupancy,
                       @JsonProperty("address") AddressDto address) {
        this.id = id;
        this.name = (name != null) ? name : "Supermarkt";
        this.details = details;
        this.coordinates = coordinates;
        this.occupancy = occupancy;
        this.address = address;
    }

}
