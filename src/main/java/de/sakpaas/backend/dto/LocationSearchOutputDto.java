package de.sakpaas.backend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "id", "name", "occupancy", "latitude", "longitude" })
public class LocationSearchOutputDto {
    private long id;
    private String name;
    private Double occupancy;
    private double lat;
    private double lon;

    @JsonCreator
    public LocationSearchOutputDto(@JsonProperty("id") long id,
            @JsonProperty("name") String name,
            @JsonProperty("occupancy") Double occupancy,
            @JsonProperty("latitude") double lat,
            @JsonProperty("longitude") double lon) {
        this.id = id;
        this.name = name;
        this.occupancy = occupancy;
        this.lat = lat;
        this.lon = lon;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getOccupancy() {
        return occupancy;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}
