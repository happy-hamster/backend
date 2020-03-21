package de.sakpaas.backend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

@JsonPropertyOrder({ "name" })
@Getter
public class TagsDto {
    private String name;

    @JsonCreator
    public TagsDto(@JsonProperty("name") String name) {
        this.name = name;
    }
}
