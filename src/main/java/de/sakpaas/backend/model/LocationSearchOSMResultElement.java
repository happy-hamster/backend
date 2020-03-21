package de.sakpaas.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LocationSearchOSMResultElement {

    private String id;
    private double lat;
    private double lon;
    private Map<String,String> tags;

    public String getName(){
        if (tags.containsKey("name")){
            return tags.get("name");
        }
        return "";

    }

}
