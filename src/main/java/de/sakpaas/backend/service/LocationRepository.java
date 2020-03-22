package de.sakpaas.backend.service;

import de.sakpaas.backend.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findById(Long id);

    List<Location> findByLatitudeBetweenAndLongitudeBetween(Double latMin, Double latMax, Double lonMin, Double lonMax);
}
