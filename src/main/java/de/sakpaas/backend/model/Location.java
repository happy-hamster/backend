package de.sakpaas.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "LOCATION")
public class Location   {

    @Id
    @Column(name = "ID", nullable = false)
    private Long id = null;

    @Column(name = "NAME", nullable = false)
    private String name = null;

    @Column(name = "LATITUDE", nullable = false)
    private Double latitude;

    @Column(name = "LONGITUDE", nullable = false)
    private Double longitude;

    @OneToOne(optional = false)
    @JoinColumn(name = "LOCATION_DETAILS_ID", referencedColumnName = "ID", nullable = false)
    private LocationDetails details = null;

    @OneToOne(optional = false)
    @JoinColumn(name = "ADDRESS_ID", referencedColumnName = "ID", nullable = false)
    private Address address = null;

}
