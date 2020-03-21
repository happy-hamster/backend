package de.sakpaas.backend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

@JsonPropertyOrder({"name", "addr:street", "addr:housenumber", "addr:postcode", "addr:city", "addr:country"})
@Getter
public class TagsDto {
    private String name;
    private String street;
    private String housenumber;
    private String postcode;
    private String city;
    private String country;

    @JsonCreator
    public TagsDto(@JsonProperty("name") String name, @JsonProperty("addr:street") String street,
                   @JsonProperty("addr:housenumber") String housenumber, @JsonProperty("addr:postcode") String postcode,
                   @JsonProperty("addr:city") String city, @JsonProperty("addr:country") String country) {
        this.name = name;
        this.street = street;
        this.housenumber = housenumber;
        this.postcode = postcode;
        this.city = city;
        this.country = country;

    }
}
