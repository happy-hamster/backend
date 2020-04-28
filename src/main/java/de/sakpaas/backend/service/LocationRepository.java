package de.sakpaas.backend.service;

import de.sakpaas.backend.model.Location;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LocationRepository extends JpaRepository<Location, Long> {
  Optional<Location> findById(Long id);

  List<Location> findByLatitudeBetweenAndLongitudeBetween(Double latMin, Double latMax,
                                                          Double lonMin, Double lonMax);

  List<Location> findByLatitudeBetweenAndLongitudeBetweenAndDetails_Type(Double latMin,
                                                                         Double latMax,
                                                                         Double lonMin,
                                                                         Double lonMax,
                                                                         List<String> type);

  @Query(value = "SELECT loc.id FROM LOCATION loc JOIN ADDRESS a ON loc.address_id = a.id "
      + "WHERE a.country=(:country)", nativeQuery = true)
  List<Long> getAllIdsForCountry(@Param("country") String country);
}
