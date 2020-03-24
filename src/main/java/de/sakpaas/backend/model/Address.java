package de.sakpaas.backend.model;

import lombok.*;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity(name = "ADDRESS")
public class Address {

    @Id
    @GeneratedValue
    @Column(name = "ID", nullable = false)
    private Long id = null;

    @Column(name = "COUNTRY")
    private String country = null;

    @Column(name = "CITY")
    private String city = null;

    @Column(name = "POSTCODE")
    private String postcode = null;

    @Column(name = "STREET")
    private String street = null;

    @Column(name = "HOUSENUMBER")
    private String housenumber = null;

    public Address(String country, String city, String postcode, String street, String housenumber) {
        this.country = country;
        this.city = city;
        this.postcode = postcode;
        this.street = street;
        this.housenumber = housenumber;
    }
}
