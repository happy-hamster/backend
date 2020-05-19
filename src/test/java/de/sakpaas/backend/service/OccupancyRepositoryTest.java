package de.sakpaas.backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import de.sakpaas.backend.RepositoryTest;
import de.sakpaas.backend.model.Address;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.LocationDetails;
import de.sakpaas.backend.model.Occupancy;
import java.security.MessageDigest;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
class OccupancyRepositoryTest extends RepositoryTest {

  @SneakyThrows
  @Test
  void testFindByLocation() {
    // Setup test data
    Location locationEdeka = new Location(1000L, "Edeka Eima", 42.0, 7.0,
        new LocationDetails("supermarket", "Mo-Fr 10-22", "Edeka"),
        new Address("DE", "Mannheim", "25565", "Handelshafen", "12a")
    );
    Location locationAldi = new Location(2000L, "Aldi", 42.001, 7.001,
        new LocationDetails("kiosk", "Fr-Sa 12-14", "Aldi"),
        new Address("FR", "Paris", "101010", "Louvre", "1")
    );
    super.insert(locationEdeka);
    super.insert(locationAldi);

    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] request = digest.digest("Alice".getBytes());

    Occupancy occupancyEdeka = new Occupancy(locationEdeka, 0.1, "TEST", request);
    Occupancy occupancyAldi = new Occupancy(locationAldi, 0.9, "TEST", request);
    super.insert(occupancyEdeka);
    super.insert(occupancyAldi);

    List<Occupancy> output = occupancyRepository.findByLocation(locationEdeka);
    assertThat(output.size()).isEqualTo(1);
    assertThat(output.get(0)).matches(o -> o.equalsIgnoreTimezone(occupancyEdeka));
  }

  @SneakyThrows
  @Test
  void testFindByUuidAndTimestampAfter() {
    // Setup test data
    Location location = new Location(1000L, "Edeka Eima", 42.0, 7.0,
        new LocationDetails("supermarket", "Mo-Fr 10-22", "Edeka"),
        new Address("DE", "Mannheim", "25565", "Handelshafen", "12a")
    );
    super.insert(location);

    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] requestAlice = digest.digest("Alice".getBytes());
    byte[] requestBob = digest.digest("Bob".getBytes());
    UUID userAlice = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");
    UUID userBob = UUID.fromString("c1d3e15b-b522-4e7c-85f9-518ea91621a4");
    ZonedDateTime timestamp = ZonedDateTime.parse("2011-12-03T10:15:30+01:00");

    Occupancy occupancyAliceAfter = new Occupancy(location, 0.1, "TEST", requestAlice, userAlice);
    Occupancy occupancyAlice = new Occupancy(location, 0.9, "TEST", requestAlice, userAlice);
    Occupancy occupancyBobAfter = new Occupancy(location, 0.9, "TEST", requestBob, userBob);
    Occupancy occupancyBob = new Occupancy(location, 0.9, "TEST", requestBob, userBob);
    occupancyAliceAfter.setTimestamp(timestamp.plusHours(1));
    occupancyAlice.setTimestamp(timestamp.minusHours(1));
    occupancyBobAfter.setTimestamp(timestamp.plusHours(1));
    occupancyBob.setTimestamp(timestamp.minusHours(1));
    super.insert(occupancyAliceAfter);
    super.insert(occupancyAlice);
    super.insert(occupancyBobAfter);
    super.insert(occupancyBob);

    List<Occupancy> output =
        occupancyRepository.findByUserUuidAndTimestampAfter(userAlice, timestamp);
    assertThat(output.size()).isEqualTo(1);
    assertThat(output.get(0)).matches(o -> o.equalsIgnoreTimezone(occupancyAliceAfter));
  }

  @SneakyThrows
  @Test
  void testFindByLocationAndUserUuidAndTimestampAfter() {
    // Setup test data
    Location locationEdeka = new Location(1000L, "Edeka Eima", 42.0, 7.0,
        new LocationDetails("supermarket", "Mo-Fr 10-22", "Edeka"),
        new Address("DE", "Mannheim", "25565", "Handelshafen", "12a")
    );
    Location locationAldi = new Location(2000L, "Aldi", 42.001, 7.001,
        new LocationDetails("kiosk", "Fr-Sa 12-14", "Aldi"),
        new Address("FR", "Paris", "101010", "Louvre", "1")
    );
    super.insert(locationEdeka);
    super.insert(locationAldi);

    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] requestAlice = digest.digest("Alice".getBytes());
    byte[] requestBob = digest.digest("Bob".getBytes());
    UUID userAlice = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");
    UUID userBob = UUID.fromString("c1d3e15b-b522-4e7c-85f9-518ea91621a4");
    ZonedDateTime timestamp = ZonedDateTime.parse("2011-12-03T10:15:30+01:00");

    Occupancy occupancyWrongLocation = new Occupancy(locationAldi, 0.1, "TEST", requestAlice, userAlice);
    Occupancy occupancyWrongUser = new Occupancy(locationEdeka, 0.1, "TEST", requestBob, userBob);
    Occupancy occupancyWrongTimestamp = new Occupancy(locationEdeka, 0.1, "TEST", requestAlice, userAlice);
    Occupancy occupancy = new Occupancy(locationEdeka, 0.1, "TEST", requestAlice, userAlice);
    occupancyWrongLocation.setTimestamp(timestamp.plusHours(1));
    occupancyWrongUser.setTimestamp(timestamp.plusHours(1));
    occupancyWrongTimestamp.setTimestamp(timestamp.minusHours(1));
    occupancy.setTimestamp(timestamp.plusHours(1));
    super.insert(occupancyWrongLocation);
    super.insert(occupancyWrongUser);
    super.insert(occupancyWrongTimestamp);
    super.insert(occupancy);

    List<Occupancy> output = occupancyRepository.findByLocationAndUserUuidAndTimestampAfter(
        locationEdeka, userAlice, timestamp);
    assertThat(output.size()).isEqualTo(1);
    assertThat(output.get(0)).matches(o -> o.equalsIgnoreTimezone(occupancy));
  }

  @SneakyThrows
  @Test
  void testFindByLocationAndRequestHashAndTimestampAfter() {
    // Setup test data
    Location locationEdeka = new Location(1000L, "Edeka Eima", 42.0, 7.0,
        new LocationDetails("supermarket", "Mo-Fr 10-22", "Edeka"),
        new Address("DE", "Mannheim", "25565", "Handelshafen", "12a")
    );
    Location locationAldi = new Location(2000L, "Aldi", 42.001, 7.001,
        new LocationDetails("kiosk", "Fr-Sa 12-14", "Aldi"),
        new Address("FR", "Paris", "101010", "Louvre", "1")
    );
    super.insert(locationEdeka);
    super.insert(locationAldi);

    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] requestAlice = digest.digest("Alice".getBytes());
    byte[] requestBob = digest.digest("Bob".getBytes());
    ZonedDateTime timestamp = ZonedDateTime.parse("2011-12-03T10:15:30+01:00");

    Occupancy occupancyWrongLocation = new Occupancy(locationAldi, 0.1, "TEST", requestAlice);
    Occupancy occupancyWrongUser = new Occupancy(locationEdeka, 0.1, "TEST", requestBob);
    Occupancy occupancyWrongTimestamp = new Occupancy(locationEdeka, 0.1, "TEST", requestAlice);
    Occupancy occupancy = new Occupancy(locationEdeka, 0.1, "TEST", requestAlice);
    occupancyWrongLocation.setTimestamp(timestamp.plusHours(1));
    occupancyWrongUser.setTimestamp(timestamp.plusHours(1));
    occupancyWrongTimestamp.setTimestamp(timestamp.minusHours(1));
    occupancy.setTimestamp(timestamp.plusHours(1));
    super.insert(occupancyWrongLocation);
    super.insert(occupancyWrongUser);
    super.insert(occupancyWrongTimestamp);
    super.insert(occupancy);

    List<Occupancy> output = occupancyRepository.findByLocationAndRequestHashAndTimestampAfter(
        locationEdeka, requestAlice, timestamp);
    assertThat(output.size()).isEqualTo(1);
    assertThat(output.get(0)).matches(o -> o.equalsIgnoreTimezone(occupancy));
  }

  @SneakyThrows
  @Test
  void testFindByUserUuidAndTimestampAfter() {
    // Setup test data
    Location location = new Location(1000L, "Edeka Eima", 42.0, 7.0,
        new LocationDetails("supermarket", "Mo-Fr 10-22", "Edeka"),
        new Address("DE", "Mannheim", "25565", "Handelshafen", "12a")
    );
    super.insert(location);

    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] requestAlice = digest.digest("Alice".getBytes());
    byte[] requestBob = digest.digest("Bob".getBytes());
    UUID userAlice = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");
    UUID userBob = UUID.fromString("c1d3e15b-b522-4e7c-85f9-518ea91621a4");
    ZonedDateTime timestamp = ZonedDateTime.parse("2011-12-03T10:15:30+01:00");

    Occupancy occupancyWrongUser = new Occupancy(location, 0.1, "TEST", requestBob, userBob);
    Occupancy occupancyWrongTimestamp = new Occupancy(location, 0.1, "TEST", requestAlice, userAlice);
    Occupancy occupancy = new Occupancy(location, 0.1, "TEST", requestAlice, userAlice);
    occupancyWrongUser.setTimestamp(timestamp.plusHours(1));
    occupancyWrongTimestamp.setTimestamp(timestamp.minusHours(1));
    occupancy.setTimestamp(timestamp.plusHours(1));
    super.insert(occupancyWrongUser);
    super.insert(occupancyWrongTimestamp);
    super.insert(occupancy);

    List<Occupancy> output = occupancyRepository.findByUserUuidAndTimestampAfter(
        userAlice, timestamp);
    assertThat(output.size()).isEqualTo(1);
    assertThat(output.get(0)).matches(o -> o.equalsIgnoreTimezone(occupancy));
  }

  @SneakyThrows
  @Test
  void testFindByRequestHashAndTimestampAfter() {
    // Setup test data
    Location location = new Location(1000L, "Edeka Eima", 42.0, 7.0,
        new LocationDetails("supermarket", "Mo-Fr 10-22", "Edeka"),
        new Address("DE", "Mannheim", "25565", "Handelshafen", "12a")
    );
    super.insert(location);

    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] requestAlice = digest.digest("Alice".getBytes());
    byte[] requestBob = digest.digest("Bob".getBytes());
    ZonedDateTime timestamp = ZonedDateTime.parse("2011-12-03T10:15:30+01:00");

    Occupancy occupancyWrongUser = new Occupancy(location, 0.1, "TEST", requestBob);
    Occupancy occupancyWrongTimestamp = new Occupancy(location, 0.1, "TEST", requestAlice);
    Occupancy occupancy = new Occupancy(location, 0.1, "TEST", requestAlice);
    occupancyWrongUser.setTimestamp(timestamp.plusHours(1));
    occupancyWrongTimestamp.setTimestamp(timestamp.minusHours(1));
    occupancy.setTimestamp(timestamp.plusHours(1));
    super.insert(occupancyWrongUser);
    super.insert(occupancyWrongTimestamp);
    super.insert(occupancy);

    List<Occupancy> output = occupancyRepository.findByRequestHashAndTimestampAfter(
        requestAlice, timestamp);
    assertThat(output.size()).isEqualTo(1);
    assertThat(output.get(0)).matches(o -> o.equalsIgnoreTimezone(occupancy));
  }
}
