package de.sakpaas.backend.dto.osmresult;

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

}
