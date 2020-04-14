package de.sakpaas.backend.service;

import de.sakpaas.backend.model.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressService {

  private final AddressRepository addressRepository;

  @Autowired
  public AddressService(AddressRepository addressRepository) {
    this.addressRepository = addressRepository;
  }

  public void save(Address address) {
    addressRepository.save(address);
  }

}
