package de.sakpaas.backend.service;

import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.Presence;
import java.time.ZonedDateTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PresenceRepository extends JpaRepository<Presence, Long> {
  Long findByLocationAndCheckOutBeforeAndCheckInAfter(Location location, ZonedDateTime before,
                                                      ZonedDateTime after);
}
