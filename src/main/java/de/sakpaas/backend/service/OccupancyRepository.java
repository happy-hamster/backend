package de.sakpaas.backend.service;

import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.Occupancy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;

public interface OccupancyRepository extends JpaRepository<Occupancy, Long> {
    List<Occupancy> findByLocationAndTimestampAfter(Location location, ZonedDateTime after);
}