package de.sakpaas.backend.model;

import static org.assertj.core.api.Assertions.assertThat;

import de.sakpaas.backend.HappyHamsterTest;
import java.time.ZonedDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
class OccupancyTest extends HappyHamsterTest {

  @Test
  public void testEqualsIgnoreTimezone() {
    ZonedDateTime timestamp1 = ZonedDateTime.parse("2011-12-03T10:15:30+01:00");
    ZonedDateTime timestamp2 = ZonedDateTime.parse("2011-12-03T09:15:30Z");

    Location location = new Location();
    Occupancy occupancy1 = new Occupancy(
        location,
        0.5,
        "TEST",
        new byte[]{(byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF},
        UUID.fromString("550e8400-e29b-11d4-a716-446655440000")
    );
    Occupancy occupancy2 = new Occupancy(
        location,
        0.5,
        "TEST",
        new byte[]{(byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF},
        UUID.fromString("550e8400-e29b-11d4-a716-446655440000")
    );

    occupancy1.setTimestamp(timestamp1);
    occupancy2.setTimestamp(timestamp2);

    assertThat(occupancy1.equalsIgnoreTimezone(occupancy2)).isTrue();
  }

  @Test
  public void testEquals() {
    ZonedDateTime timestamp1 = ZonedDateTime.parse("2011-12-03T10:15:30+01:00");
    ZonedDateTime timestamp2 = ZonedDateTime.parse("2011-12-03T09:15:30Z");

    Location location1 = new Location(1000L, "Edeka", 42.0, 7.0, null, null);
    Location location2 = new Location(1000L, "Lidl", 0.0, 0.0, null, null);
    Occupancy occupancy1 = new Occupancy(
        location1,
        0.5,
        "TEST",
        new byte[]{(byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF},
        UUID.fromString("550e8400-e29b-11d4-a716-446655440000")
    );
    Occupancy occupancy2 = new Occupancy(
        location2,
        0.5,
        "TEST",
        new byte[]{(byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF},
        UUID.fromString("550e8400-e29b-11d4-a716-446655440000")
    );
    Occupancy occupancy3 = new Occupancy(
        location1,
        0.5,
        "TEST",
        new byte[]{(byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF},
        UUID.fromString("550e8400-e29b-11d4-a716-446655440000")
    );

    occupancy1.setTimestamp(timestamp1);
    occupancy2.setTimestamp(timestamp1);
    occupancy3.setTimestamp(timestamp2);

    // Test different Locations, but same LocationId
    assertThat(occupancy1.equals(occupancy2)).isTrue();
    assertThat(occupancy2.equals(occupancy1)).isTrue();
    // Test different time zone
    assertThat(occupancy1.equals(occupancy3)).isFalse();
    assertThat(occupancy3.equals(occupancy1)).isFalse();
  }

  @Test
  public void testHashCode() {
    ZonedDateTime timestamp1 = ZonedDateTime.parse("2011-12-03T10:15:30+01:00");
    ZonedDateTime timestamp2 = ZonedDateTime.parse("2011-12-03T09:15:30Z");

    Location location1 = new Location(1000L, "Edeka", 42.0, 7.0, null, null);
    Location location2 = new Location(1000L, "Lidl", 0.0, 0.0, null, null);
    Occupancy occupancy1 = new Occupancy(
        location1,
        0.5,
        "TEST",
        new byte[]{(byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF},
        UUID.fromString("550e8400-e29b-11d4-a716-446655440000")
    );
    Occupancy occupancy2 = new Occupancy(
        location2,
        0.5,
        "TEST",
        new byte[]{(byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF},
        UUID.fromString("550e8400-e29b-11d4-a716-446655440000")
    );
    Occupancy occupancy3 = new Occupancy(
        location1,
        0.5,
        "TEST",
        new byte[]{(byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF},
        UUID.fromString("550e8400-e29b-11d4-a716-446655440000")
    );

    occupancy1.setTimestamp(timestamp1);
    occupancy2.setTimestamp(timestamp1);
    occupancy3.setTimestamp(timestamp2);

    // Test different Locations, but same LocationId
    assertThat(occupancy1.hashCode()).isEqualTo(occupancy2.hashCode());
    // Test different time zone
    assertThat(occupancy1.hashCode()).isNotEqualTo(occupancy3.hashCode());
  }
}