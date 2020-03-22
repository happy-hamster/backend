package de.sakpaas.backend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

@Getter
@JsonPropertyOrder({ "id", "lat", "lon", "tags" })
public class LocationSearchOSMResultDto {

    private long id;
    private double lat;
    private double lon;
    private TagsDto tags;

    @JsonCreator
    public LocationSearchOSMResultDto(@JsonProperty("id") long id,
            @JsonProperty("lat") double lat,
            @JsonProperty("lon") double lon,
            @JsonProperty("center") CenterDto centerDto,
            @JsonProperty("tags") TagsDto tags) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.tags = tags;
        if(centerDto != null){
            this.lat = centerDto.getLat();
            this.lon = centerDto.getLon();
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

}
