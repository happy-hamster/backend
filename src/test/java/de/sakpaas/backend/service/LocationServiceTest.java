package de.sakpaas.backend.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import de.sakpaas.backend.model.Address;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.LocationDetails;
import de.sakpaas.backend.model.Occupancy;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
class LocationServiceTest {
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
}