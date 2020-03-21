package de.sakpaas.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "LOCATION")
public class Location {
    @Column(name = "ID", nullable = false)
    @NaturalId
    @Id
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "OCCUPANCY")
    private Double occupancy;

    @Column(name = "LATITUDE", nullable = false)
    private Double latitude;

    @Column(name = "LONGITUDE", nullable = false)
    private Double longitude;
}
