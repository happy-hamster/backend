package de.sakpaas.backend.model;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity(name = "FAVORITE")
public class Favorite {
  @Id
  @GeneratedValue
  @Column(name = "ID", nullable = false)
  private Long id;

  //ManyToOne-Beziehung wird nicht dargestellt, da User in Keycloak gespeichert
  @Column(name = "USER_UUID", nullable = false)
  private UUID userUuid;

  @JoinColumn(name = "LOCATION_ID", referencedColumnName = "ID", nullable = false)
  @ManyToOne
  private Location location;

  /**
   * Constructor for creating new Favorites.
   *
   * @param userUuid the UUID of the user for which the favorite is for
   * @param location the Location of the Favorite
   */
  public Favorite(UUID userUuid, Location location) {
    this.userUuid = userUuid;
    this.location = location;
  }
}
