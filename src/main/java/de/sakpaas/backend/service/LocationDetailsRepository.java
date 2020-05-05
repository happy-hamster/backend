package de.sakpaas.backend.service;

import de.sakpaas.backend.model.LocationDetails;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LocationDetailsRepository extends JpaRepository<LocationDetails, Long> {

  Optional<LocationDetails> findById(Long id);

  @Query(value = "SELECT DISTINCT type FROM location_details", nativeQuery = true)
  List<String> getAllLocationTypes();

}
