package de.sakpaas.backend.dto.osmresult;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

@JsonPropertyOrder({"name", "addr:street", "addr:place", "addr:housenumber", "addr:postcode", "addr:city", "addr:country", "shop", "brand", "opening_hours"})
@Getter
public class OSMResultTagsDto {
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
