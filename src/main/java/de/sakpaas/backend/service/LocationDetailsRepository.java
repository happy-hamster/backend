package de.sakpaas.backend.service;

import de.sakpaas.backend.model.LocationDetails;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationDetailsRepository extends JpaRepository<LocationDetails, Long> {

  Optional<LocationDetails> findById(Long id);

}
