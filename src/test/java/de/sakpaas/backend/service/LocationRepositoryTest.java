package de.sakpaas.backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import de.sakpaas.backend.HappyHamsterTest;
import de.sakpaas.backend.model.Address;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.LocationDetails;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest
@RunWith(SpringRunner.class)
class LocationRepositoryTest extends HappyHamsterTest {

  @Autowired
  LocationRepository locationRepository;

  @Autowired
  AddressRepository addressRepository;

  @Autowired
  LocationDetailsRepository locationDetailsRepository;

  @Test
  public void testingGetByIdWithDependencyForAddressAndLocationDetails() {

    locationRepository.deleteAll();
    addressRepository.deleteAll();
    locationDetailsRepository.deleteAll();


    Address address =
        new Address("country", "testPostcode", "123456", "testStreet", "testNumber");
    Address savedAddress = addressRepository.save(address);

    LocationDetails locationDetails =
        new LocationDetails("testType", "testOpeningHours", "testBrand");
    LocationDetails savedLocationDetails = locationDetailsRepository.save(locationDetails);

    Location location = new Location(1L, "LIDL", 41.0D, 8.0D, savedLocationDetails, savedAddress);
    locationRepository.save(location);

    Optional<Location> optionalLocation = locationRepository.findById(1L);
    assertThat(optionalLocation.isPresent()).isTrue();

    locationRepository.deleteAll();
    addressRepository.deleteAll();
    locationDetailsRepository.deleteAll();

  }

}