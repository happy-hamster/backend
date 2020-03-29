package de.sakpaas.backend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "PRESENCE")
public class Presence {

    @Id
    @GeneratedValue
    @Column(name = "ID", nullable = false)
    private Long id;

    @JoinColumn(name = "LOCATION_ID", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false)
    private Location location;

    @Column(name = "CHECK_IN_DATE")
    private ZonedDateTime checkIn;

    @Column(name = "CHECK_OUT_DATE")
    private ZonedDateTime checkOut;

    public Presence(Location location, ZonedDateTime checkIn, ZonedDateTime checkOut) {
        this.location = location;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }
}
