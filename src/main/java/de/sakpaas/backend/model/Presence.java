package de.sakpaas.backend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "PRESENCE")
public class Presence {
  @Column(name = "ID", nullable = false)
  @NaturalId
  @Id
  private String id;
  @JoinColumn(name = "LOCATION_ID", referencedColumnName = "ID", nullable = false)
  @ManyToOne(optional = false)
  private Location location;
  @Column(name = "CHECK_IN_DATE")
  private ZonedDateTime checkIn;
  @Column(name = "CHECK_OUT_DATE")
  private ZonedDateTime checkOut;

  public Presence(String locationId, Date checkIn, Date checkOut) {
    this.locationId = locationId;
    this.checkIn = checkIn;
    this.checkOut = checkOut;
  }
}
