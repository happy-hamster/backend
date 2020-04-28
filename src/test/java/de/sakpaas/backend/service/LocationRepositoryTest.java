package de.sakpaas.backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import de.sakpaas.backend.HappyHamsterTest;
import de.sakpaas.backend.model.Address;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.LocationDetails;
import java.util.Arrays;
import java.util.List;
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
        new Address("testCountry", "testPostcode", "123456", "testStreet", "testNumber");
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

  @Test
  void findByLatitudeBetweenAndLongitudeBetweenAndDetails_TypeIn() {
    locationRepository.deleteAll();
    addressRepository.deleteAll();
    locationDetailsRepository.deleteAll();

    Address address =
        new Address("testCountry", "testPostcode", "123456", "testStreet", "testNumber");
    Address savedAddress = addressRepository.save(address);

    LocationDetails locationDetails =
        new LocationDetails("testType", "testOpeningHours", "testBrand");
    LocationDetails savedLocationDetails = locationDetailsRepository.save(locationDetails);

    Location location = new Location(1L, "LIDL", 41.0D, 8.0D, savedLocationDetails, savedAddress);
    locationRepository.save(location);


    Address address1 =
        new Address("testCountry", "testPostcode", "123456", "testStreet", "testNumber");
    Address savedAddress1 = addressRepository.save(address1);

    LocationDetails locationDetails1 =
        new LocationDetails("testType1", "testOpeningHours", "testBrand");
    LocationDetails savedLocationDetails1 = locationDetailsRepository.save(locationDetails1);

    Location location1 = new Location(2L, "LIDL", 40.0D, 7.0D, savedLocationDetails, savedAddress);
    locationRepository.save(location1);

    List<Location>
        list = locationRepository
        .findByLatitudeBetweenAndLongitudeBetweenAndDetails_TypeIn(30.D, 42.D, 6.D, 9.0,
            Arrays.asList("testtype1"));

    assertThat(list.size()).isEqualTo(1);
    assertThat(list.get(0)).isEqualTo(location1);
  }
}