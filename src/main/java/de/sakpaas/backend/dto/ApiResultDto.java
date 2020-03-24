package de.sakpaas.backend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

import java.util.List;

@JsonPropertyOrder({ "elements" })
@Getter
public class ApiResultDto {

    private List<LocationOSMDto> elements;

    @JsonCreator
    public ApiResultDto(@JsonProperty("elements") List<LocationOSMDto> elements) {
        this.elements = elements;
    }

}
