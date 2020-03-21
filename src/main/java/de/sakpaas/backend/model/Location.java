package de.sakpaas.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Location {

    private long id;
    private String name;
    private Double occupancy;
    private double latitude;
    private double longitude;

}
