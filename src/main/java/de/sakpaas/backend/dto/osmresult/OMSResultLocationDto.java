package de.sakpaas.backend.dto.osmresult;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.sakpaas.backend.dto.locationresult.LocationResultCoordinatesDto;
import lombok.Getter;

@Getter
@JsonPropertyOrder({"id", "coordinates", "tags"})
public class OMSResultLocationDto {

    private long id;
    private LocationResultCoordinatesDto coordinates;
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
            this.coordinates = new LocationResultCoordinatesDto(lat, lon);
        } else {
            this.coordinates = new LocationResultCoordinatesDto(center.getLat(), center.getLon());
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
