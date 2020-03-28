package de.sakpaas.backend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.sakpaas.backend.model.Address;
import de.sakpaas.backend.model.LocationDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.time.ZonedDateTime;

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

    @Data
    public static class LocationResultLocationDetailsDto {

        private String type;
        private String openingHours;
        private String brand;

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
        private ZonedDateTime lastestReport;

    }

}
