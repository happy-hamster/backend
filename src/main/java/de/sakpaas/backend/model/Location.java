package de.sakpaas.backend.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "LOCATION")
@Table(name = "LOCATION", indexes = {
    @Index(name = "lat_lon_index", columnList = "LATITUDE,LONGITUDE")})
public class Location {

  @Id
  @Column(name = "ID", nullable = false)
  private Long id;

  @Column(name = "NAME", nullable = false)
  private String name = null;

  @Column(name = "LATITUDE", nullable = false)
  private Double latitude;

  @Column(name = "LONGITUDE", nullable = false)
  private Double longitude;

  @OneToOne(optional = false)
  @JoinColumn(name = "LOCATION_DETAILS_ID", referencedColumnName = "ID", nullable = false)
  private LocationDetails details;

  @OneToOne(optional = false)
  @JoinColumn(name = "ADDRESS_ID", referencedColumnName = "ID", nullable = false)
  private Address address;

}
