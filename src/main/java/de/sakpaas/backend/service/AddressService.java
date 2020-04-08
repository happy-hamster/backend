package de.sakpaas.backend.service;

import de.sakpaas.backend.model.Address;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressService {

  private AddressRepository addressRepository;

  @Autowired
  public AddressService(AddressRepository addressRepository) {
    this.addressRepository = addressRepository;
  }

  public Optional<Address> getById(long id) {
    return addressRepository.findById(id);
  }

  public Address save(Address address) {
    return addressRepository.save(address);
  }

}
