package de.sakpaas.backend.model;

import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "PRESENCE")
public class Presence {

  @Id
  @GeneratedValue
  @Column(name = "ID", nullable = false)
  private Long id;

  @JoinColumn(name = "LOCATION_ID", referencedColumnName = "ID", nullable = false)
  @ManyToOne(optional = false)
  private Location location;

  @Column(name = "CHECK_IN_DATE")
  private ZonedDateTime checkIn;

  @Column(name = "CHECK_OUT_DATE")
  private ZonedDateTime checkOut;

  /**
   * Creates a new {@link Presence} from a {@link Location}.
   *
   * @param location the {@link Location}
   * @param checkIn  the check in time
   * @param checkOut the check out time
   */
  public Presence(Location location, ZonedDateTime checkIn, ZonedDateTime checkOut) {
    this.location = location;
    this.checkIn = checkIn;
    this.checkOut = checkOut;
  }
}
