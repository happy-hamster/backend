package de.sakpaas.backend.model;

import lombok.*;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "LOCATION_DETAILS")
public class LocationDetails {

    @Id
    @GeneratedValue
    @Column(name = "ID", nullable = false)
    private Long id = null;

    @Column(name = "TYPE")
    private String type = null;

    @Column(name = "OPENING_HOURS")
    private String openingHours = null;

    public LocationDetails(String type, String openingHours) {
        this.type = type;
        this.openingHours = openingHours;
    }

}
