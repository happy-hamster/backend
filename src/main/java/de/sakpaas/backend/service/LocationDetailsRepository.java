package de.sakpaas.backend.service;

import de.sakpaas.backend.model.LocationDetails;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LocationDetailsRepository extends JpaRepository<LocationDetails, Long> {

  Optional<LocationDetails> findById(Long id);

  @Query(value = "SELECT lower(brand) "
      + "FROM location_details "
      + "WHERE brand IS NOT null "
      + "GROUP BY lower(brand)",
      nativeQuery = true)
  Set<String> getAllBrandNamesLower();

  @Query(value = "SELECT DISTINCT type FROM location_details", nativeQuery = true)
  List<String> getAllLocationTypes();

}
