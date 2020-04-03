package de.sakpaas.backend.service;

import de.sakpaas.backend.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findById(Long id);

    List<Location> findByLatitudeBetweenAndLongitudeBetween(Double latMin, Double latMax, Double lonMin, Double lonMax);


    @Query(value = "SELECT ID FROM LOCATION", nativeQuery = true)
    List<Long> getAllIds();
}
