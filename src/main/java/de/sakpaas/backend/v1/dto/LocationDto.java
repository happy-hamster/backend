package de.sakpaas.backend.v1.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"id", "name", "details", "coordinates", "occupancy", "address"})
public class LocationDto {

    private long id;
    private String name;
    private String country;
    private String city;
    private String postcode;
    private String street;
    private String housenumber;
    private Double occupancy;
    private double latitude;
    private double longitude;

}
