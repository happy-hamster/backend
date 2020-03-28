package de.sakpaas.backend.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@Entity(name = "ADDRESS")
public class Address {

    @Id
    @GeneratedValue
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "COUNTRY")
    private String country;

    @Column(name = "CITY")
    private String city;

    @Column(name = "POSTCODE")
    private String postcode;

    @Column(name = "STREET")
    private String street;

    @Column(name = "HOUSENUMBER")
    private String housenumber;

    public Address(String country, String city, String postcode, String street, String housenumber) {
        this.country = country;
        this.city = city;
        this.postcode = postcode;
        this.street = street;
        this.housenumber = housenumber;
    }
}
