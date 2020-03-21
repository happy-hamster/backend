package de.sakpaas.backend.service;

import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.Presence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;

public interface PresenceRepository extends JpaRepository<Presence, Long> {
  Long findByLocationAndCheckOutBeforeAndCheckInAfter(Location location, ZonedDateTime before,
          ZonedDateTime after);
}
