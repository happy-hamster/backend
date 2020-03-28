package de.sakpaas.backend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

import java.util.List;

@JsonPropertyOrder({"elements"})
@Getter
public class OSMResultLocationListDto {

    private List<OMSResultLocationDto> elements;

    @JsonCreator
    public OSMResultLocationListDto(@JsonProperty("elements") List<OMSResultLocationDto> elements) {
        this.elements = elements;
    }

    @Getter
    @JsonPropertyOrder({"id", "coordinates", "tags"})
    public static class OMSResultLocationDto {

        private long id;
        private LocationResultLocationDto.LocationResultCoordinatesDto coordinates;
        private OSMResultTagsDto tags;

        @JsonCreator
        public OMSResultLocationDto(@JsonProperty("id") long id,
                                    @JsonProperty("lat") double lat,
                                    @JsonProperty("lon") double lon,
                                    @JsonProperty("center") OSMResultCenterDto center,
                                    @JsonProperty("tags") OSMResultTagsDto tags) {
            this.id = id;
            this.tags = tags;
            if (center == null) {
                this.coordinates = new LocationResultLocationDto.LocationResultCoordinatesDto(lat, lon);
            } else {
                this.coordinates = new LocationResultLocationDto.LocationResultCoordinatesDto(center.getLat(), center.getLon());
            }
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
    public static class OSMResultCenterDto {
        private double lat;
        private double lon;

        @JsonCreator
        public OSMResultCenterDto(@JsonProperty("lat") double lat,
                                  @JsonProperty("lon") double lon) {
            this.lat = lat;
            this.lon = lon;
        }
    }

    @JsonPropertyOrder({"name", "addr:street", "addr:place", "addr:housenumber", "addr:postcode", "addr:city", "addr:country", "shop", "brand", "opening_hours"})
    @Getter
    public static class OSMResultTagsDto {
        private String name;
        private String street;
        private String housenumber;
        private String postcode;
        private String city;
        private String country;
        private String type;
        private String brand;
        private String openingHours;

        @JsonCreator
        public OSMResultTagsDto(@JsonProperty("name") String name, @JsonProperty("addr:street") String street,
                                @JsonProperty("addr:housenumber") String housenumber, @JsonProperty("addr:postcode") String postcode,
                                @JsonProperty("addr:city") String city, @JsonProperty("addr:country") String country,
                                @JsonProperty("addr:place") String place, @JsonProperty("shop") String type,
                                @JsonProperty("brand") String brand, @JsonProperty("opening_hours") String openingHours) {
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
