package de.sakpaas.backend.model;

import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "OCCUPANCY")
public class Occupancy {

  @Id
  @GeneratedValue
  @Column(name = "ID")
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

  /**
   * Creates a new {@link Occupancy} for a {@link Location}.
   *
   * @param location   the {@link Location}
   * @param occupancy  the occupancy (from 0.0 to 1.0)
   * @param clientType the client type (eg. IOT, WEB_CLIENT)
   */
  public Occupancy(Location location, Double occupancy, String clientType) {
    this.location = location;
    this.occupancy = occupancy;
    this.timestamp = ZonedDateTime.now();
    this.clientType = clientType;
  }
}
