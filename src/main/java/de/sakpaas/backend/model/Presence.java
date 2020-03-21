package de.sakpaas.backend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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
  @Column(name = "LOCATION_ID")
  private String locationId;
  @Column(name = "CHECK_IN_DATE")
  private Date checkIn;
  @Column(name = "CHECK_OUT_DATE")
  private Date checkOut;

  public Presence(String locationId, Date checkIn, Date checkOut) {
    this.locationId = locationId;
    this.checkIn = checkIn;
    this.checkOut = checkOut;
  }
}
