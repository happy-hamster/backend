package de.sakpaas.backend.service;


import static org.assertj.core.api.Assertions.assertThat;

import de.sakpaas.backend.HappyHamsterTest;
import de.sakpaas.backend.model.Address;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
class AddressServiceTest extends HappyHamsterTest {

  @Autowired
  AddressRepository addressRepository;

  @Test
  void saveAndFindById() {
    addressRepository.deleteAll();

    Address address =
        new Address("country", "testPostcode", "123456", "testStreet", "testNumber");
    Address savedAddress = addressRepository.save(address);

    assertThat(addressRepository.findById(savedAddress.getId()).isPresent()).isTrue();
    addressRepository.deleteAll();
  }
}