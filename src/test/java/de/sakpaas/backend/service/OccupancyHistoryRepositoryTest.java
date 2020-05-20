package de.sakpaas.backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import de.sakpaas.backend.RepositoryTest;
import de.sakpaas.backend.model.Address;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.LocationDetails;
import de.sakpaas.backend.model.OccupancyHistory;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
class OccupancyHistoryRepositoryTest extends RepositoryTest {

  @SneakyThrows
  @Test
  void testFindByLocationIn() {
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

    OccupancyHistory occupancyHistoryEdeka = new OccupancyHistory(locationEdeka, 10);
    OccupancyHistory occupancyHistoryAldi = new OccupancyHistory(locationAldi, 144);
    super.insert(occupancyHistoryEdeka);
    super.insert(occupancyHistoryAldi);

    // Test
    Set<OccupancyHistory> output = occupancyHistoryRepository.findByLocationIn(
        new HashSet<>(Collections.singletonList(locationEdeka)));
    assertThat(output.size()).isEqualTo(1);
    assertThat(output.iterator().next()).isEqualTo(occupancyHistoryEdeka);
  }
}
