package de.sakpaas.backend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "OCCUPANCY")
public class Occupancy {
    @Id
    @Column(name = "ID")
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "LOCATION_ID", referencedColumnName = "ID", nullable = false)
    private Location location;

    @Column(name = "OCCUPANCY", nullable = false)
    private Double occupancy;

    @Column(name = "TIMESTAMP")
    private ZonedDateTime timestamp;

    @Column(name = "CLIENT_TYPE")
    private String clientType;

    public Occupancy(Location location, Double occupancy, String clientType) {
        this.location = location;
        this.occupancy = occupancy;
        this.timestamp = ZonedDateTime.now();
        this.clientType = clientType;
    }
}
