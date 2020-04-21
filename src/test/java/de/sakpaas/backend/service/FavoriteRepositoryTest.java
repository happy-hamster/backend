package de.sakpaas.backend.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

import de.sakpaas.backend.HappyHamsterTest;
import de.sakpaas.backend.model.Address;
import de.sakpaas.backend.model.Favorite;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.LocationDetails;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class FavoriteRepositoryTest extends HappyHamsterTest {

  @Autowired
  FavoriteRepository favoriteRepository;
  @Autowired
  LocationRepository locationRepository;
  @Autowired
  AddressRepository addressRepository;
  @Autowired
  LocationDetailsRepository locationDetailsRepository;

  private List<Location> locationList = new ArrayList<>();
  private List<UUID> userList = new ArrayList<>();
  private List<Favorite> favoriteList = new ArrayList<>();

  @Before
  public void setup() {
    locationList.add(location(1L));
    locationList.add(location(2L));
    locationList.add(location(3L));
    userList.add(UUID.randomUUID());
    userList.add(UUID.randomUUID());
    userList.add(UUID.randomUUID());

    Long favCount = 1L;
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        favoriteList.add(favorite(favCount, userList.get(j), locationList.get(i)));
      }
    }
  }

  @After
  public void teardown() {
    favoriteRepository.deleteAll();
    locationRepository.deleteAll();
  }

  @Test
  public void shouldGetFavoritesByLocation() {
    List<Favorite> result = favoriteRepository.findByLocation(locationList.get(0));

    result.forEach(favorite ->
        assertThat("every Item should have first Location", favorite.getLocation(),
            equalTo(locationList.get(0))));
    assertThat("result should have first Favorite", result, hasItem(favoriteList.get(0)));
    assertThat("result should have second Favorite", result, hasItem(favoriteList.get(1)));
    assertThat("result should have third Favorite", result, hasItem(favoriteList.get(2)));
  }

  @Test
  public void shouldGetFavoritesByUser() {
    List<Favorite> result = favoriteRepository.findByUserUuid(userList.get(2));

    result.forEach(favorite ->
        assertThat("every Item should have third user", favorite.getUserUuid(),
            equalTo(userList.get(2))));
    assertThat("result should have second Favorite", result, hasItem(favoriteList.get(2)));
    assertThat("result should have fifth Favorite", result, hasItem(favoriteList.get(5)));
    assertThat("result should have eighths Favorite", result, hasItem(favoriteList.get(8)));
  }

  private Location location(Long id) {
    LocationDetails locationDetails = locationDetailsRepository.save(new LocationDetails());
    Address address = addressRepository.save(new Address());
    return locationRepository
        .save(new Location(id, "Supermarkt: " + id, 0.0D, 0.0D, locationDetails, address));
  }

  private Favorite favorite(Long id, UUID uuid, Location location) {
    Favorite favorite = new Favorite(uuid, location);
    favorite.setId(id);
    return favoriteRepository.save(favorite);
  }
}
