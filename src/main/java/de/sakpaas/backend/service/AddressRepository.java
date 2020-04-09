package de.sakpaas.backend.service;

import de.sakpaas.backend.model.Address;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {

  Optional<Address> findById(Long id);

}
