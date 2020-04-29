package de.sakpaas.backend.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import de.sakpaas.backend.HappyHamsterTest;
import de.sakpaas.backend.model.Address;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.LocationDetails;
import de.sakpaas.backend.model.Occupancy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@EnableConfigurationProperties(KeycloakSpringBootProperties.class)
@SpringBootTest
class LocationServiceTest extends HappyHamsterTest {
  @Autowired
  private LocationService locationService;
  @Autowired
  private LocationRepository locationRepository;
  @Autowired
  private PresenceRepository presenceRepository;
  @Autowired
  private OccupancyRepository occupancyRepository;
  @Autowired
  private LocationDetailsService locationDetailsService;
  @Autowired
  private AddressService addressService;
  @Autowired
  private OccupancyService occupancyService;
  @Autowired
  private PresenceService presenceService;
  @Autowired
  private AddressRepository addressRepository;
  @Autowired
  private LocationDetailsRepository locationDetailsRepository;

  @Test
  void shouldDeleteLocations() {
    assertThat(locationRepository.count(), equalTo(0L));
    Location penny = locationService.save(
        new Location(1L, "Penny", 0.0, 0.0, locationDetailsService.save(new LocationDetails()),
            addressService.save(new Address())));
    Location aldi = locationService.save(
        new Location(2L, "Aldi", 0.0, 0.0, locationDetailsService.save(new LocationDetails()),
            addressService.save(new Address())));
    Location lidl = locationService.save(
        new Location(3L, "Lidl", 0.0, 0.0, locationDetailsService.save(new LocationDetails()),
            addressService.save(new Address())));

    occupancyService.save(new Occupancy(penny, 1.0, "costumer"));
    presenceService.addNewCheckin(penny);
    occupancyService.save(new Occupancy(aldi, 0.33, "costumer"));
    presenceService.addNewCheckin(aldi);
    occupancyService.save(new Occupancy(lidl, 0.66, "costumer"));
    presenceService.addNewCheckin(lidl);

    assertThat(locationRepository.count(), equalTo(3L));
    assertThat(presenceRepository.count(), equalTo(3L));
    assertThat(occupancyRepository.count(), equalTo(3L));

    locationService.delete(penny);
    locationService.delete(aldi);
    locationService.delete(lidl);

    assertThat(locationRepository.count(), equalTo(0L));
    assertThat(presenceRepository.count(), equalTo(0L));
    assertThat(occupancyRepository.count(), equalTo(0L));
  }

  @Test
  void findByCoordinates() {
    locationRepository.deleteAll();
    addressRepository.deleteAll();
    locationDetailsRepository.deleteAll();

    Address address =
        new Address("tc", "tp", "123456", "ts", "tn");
    Address savedAddress = addressRepository.save(address);

    LocationDetails locationDetails =
        new LocationDetails("tt", "toh", "tb");
    LocationDetails savedLocationDetails = locationDetailsRepository.save(locationDetails);

    Location location = new Location(1L, "LIDL", 29.95, 5.95, savedLocationDetails, savedAddress);
    locationRepository.save(location);


    Address address1 =
        new Address("tc", "tp", "123456", "ts", "tn");
    Address savedAddress1 = addressRepository.save(address1);

    LocationDetails locationDetails1 =
        new LocationDetails("tt1", "toh", "tb");
    LocationDetails savedLocationDetails1 = locationDetailsRepository.save(locationDetails1);

    Location location1 =
        new Location(2L, "LIDL", 30.05, 6.05, savedLocationDetails1, savedAddress1);
    locationRepository.save(location1);

    List<Location>
        list = locationService.findByCoordinates(30.D, 6.D, Collections.singletonList("tt1"));


    Assertions.assertThat(list.size()).isEqualTo(1);
    Assertions.assertThat(list.get(0)).isEqualTo(location1);

    list = locationService.findByCoordinates(30.D, 6.D, null);

    Assertions.assertThat(list.size()).isEqualTo(2);
    Assertions.assertThat(list.contains(location)).isTrue();
    Assertions.assertThat(list.contains(location1)).isTrue();

    list = locationService.findByCoordinates(30.D, 6.D, new ArrayList<String>());

    Assertions.assertThat(list.size()).isEqualTo(2);
    Assertions.assertThat(list.contains(location)).isTrue();
    Assertions.assertThat(list.contains(location1)).isTrue();

    locationRepository.deleteAll();
    addressRepository.deleteAll();
    locationDetailsRepository.deleteAll();

  }
}