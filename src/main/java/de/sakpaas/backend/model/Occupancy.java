package de.sakpaas.backend.model;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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

  @Column(name = "REQUEST_HASH")
  private byte[] requestHash;

  /**
   * Creates a new {@link Occupancy} for a {@link Location}.
   *
   * @param location    the {@link Location}
   * @param occupancy   the occupancy (from 0.0 to 1.0)
   * @param clientType  the client type (eg. IOT, WEB_CLIENT)
   * @param requestHash the hash computed of the request sender
   */
  public Occupancy(Location location, Double occupancy, String clientType, byte[] requestHash) {
    this.location = location;
    this.occupancy = occupancy;
    this.timestamp = ZonedDateTime.now();
    this.clientType = clientType;
    this.requestHash = requestHash;
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

  /**
   * Compares two {@link Occupancy} while ignoring the time zone of the timestamp. Should be equal:
   * <ul>
   *   <li>2020-05-11T00:29:49+02:00[Europe/Berlin]</li>
   *   <li>2020-05-10T22:29:49Z[UTC]</li>
   * </ul>
   *
   * @param other the {@link Occupancy} to compare to
   * @return whether or not the {@link Occupancy}s are
   */
  public boolean equalsIgnoreTimezone(Occupancy other) {
    return Objects.equals(id, other.id)
        && Objects.equals(location, other.location)
        && Objects.equals(occupancy, other.occupancy)
        && timestamp.toInstant().equals(other.timestamp.toInstant())
        && Objects.equals(clientType, other.clientType)
        && Objects.equals(userUuid, other.userUuid)
        && Arrays.equals(requestHash, other.requestHash);
  }
}
