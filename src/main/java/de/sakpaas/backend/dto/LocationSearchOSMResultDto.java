package de.sakpaas.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LocationSearchOSMResultDto {

    private long id;
    private double lat;
    private double lon;
    private Map<String, String> tags;

    public String getName() {
        if (tags.containsKey("name")) {
            return tags.get("name");
        }
        return "";

    }

}
