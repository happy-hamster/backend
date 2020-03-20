package de.sakpaas.backend.model;

public class LocationModel {

    private final String id;
    private final String name;
    private final Double occupancy;
    private final Double latitude;
    private final Double longitude;

    public LocationModel(String id, String name, Double occupancy, Double latitude, Double longitude) {
        this.id = id;
        this.name = name;
        this.occupancy = occupancy;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Double getOccupancy() {
        return occupancy;
    }

    public Double getLatitude() {
        return latitude;
    }

}
