package de.sakpaas.backend.dto.locationresult;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

@Getter
@JsonPropertyOrder({"id", "name", "details", "coordinates", "occupancy", "address"})
public class LocationResultLocationDto {

    private long id;
    private String name;
    private LocationResultLocationDetailsDto details;
    private LocationResultCoordinatesDto coordinates;
    private LocationResultOccupancyDto occupancy;
    private LocationResultAddressDto address;

    @JsonCreator
    public LocationResultLocationDto(@JsonProperty("id") long id,
                                     @JsonProperty("name") String name,
                                     @JsonProperty("details") LocationResultLocationDetailsDto details,
                                     @JsonProperty("coordinates") LocationResultCoordinatesDto coordinates,
                                     @JsonProperty("occupancy") LocationResultOccupancyDto occupancy,
                                     @JsonProperty("address") LocationResultAddressDto address) {
        this.id = id;
        this.name = (name != null) ? name : "Supermarkt";
        this.details = details;
        this.coordinates = coordinates;
        this.occupancy = occupancy;
        this.address = address;
    }

}
