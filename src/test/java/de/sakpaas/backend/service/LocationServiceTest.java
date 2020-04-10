package de.sakpaas.backend.service;

import de.sakpaas.backend.model.Address;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.LocationDetails;
import de.sakpaas.backend.model.Occupancy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

@SpringBootTest
@ComponentScan
class LocationServiceTest {
  @Autowired
  private LocationService locationService;
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

    locationService.delete(penny);
    locationService.delete(aldi);
    locationService.delete(lidl);
  }
}