package de.sakpaas.backend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
//@JsonPropertyOrder({"id", "name", "occupancy", "latitude", "longitude", "type"})
public class LocationSearchOutputDto {
    private long id;
    private String name;
    private Double occupancy;
    private double lat;
    private double lon;
    private String street;
    private String housenumber;
    private String postcode;
    private String city;
    private String country;
    private String type;

    @JsonCreator
    public LocationSearchOutputDto(@JsonProperty("id") long id,
                                   @JsonProperty("name") String name,
                                   @JsonProperty("occupancy") Double occupancy,
                                   @JsonProperty("latitude") double lat,
                                   @JsonProperty("longitude") double lon,
                                   @JsonProperty("street") String street,
                                   @JsonProperty("housenumber") String housenumber,
                                   @JsonProperty("postcode") String postcode,
                                   @JsonProperty("city") String city,
                                   @JsonProperty("country") String country,
                                   @JsonProperty("type") String type) {
        this.id = id;
        if (name == null) {
            this.name = "Supermarkt";
        } else {
            this.name = name;
        }
        this.occupancy = occupancy;
        this.lat = lat;
        this.lon = lon;
        this.street = street;
        this.housenumber = housenumber;
        this.postcode = postcode;
        this.city = city;
        this.country = country;
        this.type = type;
    }

}
