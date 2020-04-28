package de.sakpaas.backend.service;

import de.sakpaas.backend.model.LocationDetails;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LocationDetailsRepository extends JpaRepository<LocationDetails, Long> {

  Optional<LocationDetails> findById(Long id);

  @Query(value = "select lower(brand) from location_details where brand is not null group by brand",
      nativeQuery = true)
  Set<String> getAllBrandNamesLower();
}
