package de.sakpaas.backend.service;

import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.Occupancy;
import de.sakpaas.backend.model.Presence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;

public interface PresenceRepository extends JpaRepository<Presence, Long> {

  List<Presence> findByLocationAndCheckOutBeforeAndCheckInAfter(Location location, ZonedDateTime after);

}
