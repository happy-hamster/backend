package de.sakpaas.backend.service;

import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.Occupancy;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OccupancyRepository extends JpaRepository<Occupancy, Long> {
  List<Occupancy> findByLocationAndTimestampAfter(Location location, ZonedDateTime after);

  List<Occupancy> deleteByLocation(Location location);
}