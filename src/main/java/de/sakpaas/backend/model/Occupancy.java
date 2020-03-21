package de.sakpaas.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "OCCUPANCY")
public class Occupancy {
    @Id
    @Column(name = "ID")
    @NaturalId
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "LOCATION_ID", referencedColumnName = "ID", nullable = false)
    private Location location;

    @Column(name = "OCCUPANCY", nullable = false)
    private Double occupancy;

    @Column(name = "TIMESTAMP")
    private ZonedDateTime timestamp;
}
