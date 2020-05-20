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
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
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

  @Column(name = "HISTORY_PROCESSED", columnDefinition = "boolean not null default false")
  private boolean historyProcessed = false;

  /**
   * Creates a new {@link Occupancy} for a {@link Location}.
   *
   * @param location    the {@link Location}
   * @param occupancy   the occupancy (from 0.0 to 1.0)
   * @param clientType  the client type (eg. IOT, WEB_CLIENT)
   * @param requestHash the hash computed of the request sender
   */
  public Occupancy(Location location, Double occupancy, String clientType,
                   @NotNull byte[] requestHash) {
    this.location = location;
    this.occupancy = occupancy;
    this.timestamp = ZonedDateTime.now();
    this.clientType = clientType;
    this.requestHash = requestHash;
  }

  /**
   * Creates a new {@link Occupancy} for a {@link Location}.
   *
   * @param location    the {@link Location}
   * @param occupancy   the occupancy (from 0.0 to 1.0)
   * @param clientType  the client type (eg. IOT, WEB_CLIENT)
   * @param requestHash the hash computed of the request sender
   * @param userUuid    the UUID of the user which saved the occupancy
   */
  public Occupancy(Location location, Double occupancy, String clientType,
                   @NotNull byte[] requestHash, UUID userUuid) {
    this.location = location;
    this.occupancy = occupancy;
    this.timestamp = ZonedDateTime.now();
    this.clientType = clientType;
    this.requestHash = requestHash;
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
   * @return whether or not the {@link Occupancy}s are equal (ignoring time zone)
   */
  public boolean equalsIgnoreTimezone(Occupancy other) {
    return Objects.equals(id, other.id)
        && Objects.equals(location, other.location)
        && Objects.equals(occupancy, other.occupancy)
        && Objects.equals(
            (timestamp == null) ? null : timestamp.toInstant(),
            (other.timestamp == null) ? null : other.timestamp.toInstant())
        && Objects.equals(clientType, other.clientType)
        && Objects.equals(userUuid, other.userUuid)
        && Arrays.equals(requestHash, other.requestHash);
  }

  /**
   * Checks for equality of two {@link Occupancy}s. The {@link Location} do not have to be the same,
   * but have to have the same ID.
   *
   * @param o the other {@link Occupancy}
   * @return whether or not the given {@link Occupancy}s are equal
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Occupancy other = (Occupancy) o;
    return Objects.equals(id, other.id)
        && Objects.equals(
            (location == null) ? null : location.getId(),
            (other.location == null) ? null : other.location.getId())
        && Objects.equals(occupancy, other.occupancy)
        && Objects.equals(timestamp, other.timestamp)
        && Objects.equals(clientType, other.clientType)
        && Objects.equals(userUuid, other.userUuid)
        && Arrays.equals(requestHash, other.requestHash);
  }

  /**
   * Computes the hash code of this {@link Occupancy}.
   *
   * @return thr hash code
   */
  @Override
  public int hashCode() {
    int result = Objects.hash(
        id, (location == null) ? -1 : location.getId(), occupancy, timestamp, clientType, userUuid);
    result = 31 * result + Arrays.hashCode(requestHash);
    return result;
  }
}
