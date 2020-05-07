package de.sakpaas.backend.model;

import java.time.ZonedDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "OCCUPANCY")
@Table(indexes = {@Index(name = "timestamp_index", columnList = "TIMESTAMP")})
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

  @Column(name = "CLIENT_TYPE", length = 10)
  private String clientType;

  // ManyToOne-Relationship is not displayed as user is managed in keycloak
  @Column(name = "USER_UUID")
  private UUID userUuid;

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

  /**
   * Creates a new {@link Occupancy} for a {@link Location}.
   *
   * @param location   the {@link Location}
   * @param occupancy  the occupancy (from 0.0 to 1.0)
   * @param clientType the client type (eg. IOT, WEB_CLIENT)
   * @param userUuid   the UUID of the user which saved the occupancy
   */
  public Occupancy(Location location, Double occupancy, String clientType, UUID userUuid) {
    this.location = location;
    this.occupancy = occupancy;
    this.timestamp = ZonedDateTime.now();
    this.clientType = clientType;
    this.userUuid = userUuid;
  }
}
