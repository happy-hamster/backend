package de.sakpaas.backend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

@Getter
@JsonPropertyOrder({ "id", "coordinates", "tags" })
public class LocationOSMDto {

    private long id;
    private CoordinatesDto coordinates;
    private TagsOSMDto tags;

    @JsonCreator
    public LocationOSMDto(@JsonProperty("id") long id,
                          @JsonProperty("lat") double lat,
                          @JsonProperty("lon") double lon,
                          @JsonProperty("center") CenterDto center,
                          @JsonProperty("tags") TagsOSMDto tags) {
        this.id = id;
        this.tags = tags;
        if(center == null){
            this.coordinates = new CoordinatesDto(lat, lon);
        }
        else {
            this.coordinates = new CoordinatesDto(center.getLat(), center.getLon());
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

}
