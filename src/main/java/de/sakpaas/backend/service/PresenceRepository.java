package de.sakpaas.backend.service;

import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.Presence;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PresenceRepository extends JpaRepository<Presence, Long> {
  List<Presence> findByLocation(Location location);
}
