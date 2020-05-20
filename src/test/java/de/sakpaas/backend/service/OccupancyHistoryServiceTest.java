package de.sakpaas.backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import de.sakpaas.backend.RepositoryTest;
import de.sakpaas.backend.model.Address;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.LocationDetails;
import de.sakpaas.backend.model.Occupancy;
import de.sakpaas.backend.model.OccupancyHistory;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
class OccupancyHistoryServiceTest extends RepositoryTest {

  @Autowired
  OccupancyHistoryService occupancyHistoryService;

  @MockBean
  OccupancyService occupancyService;

  @Test
  void testAggregateHistory() {
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

    OccupancyHistory occupancyHistoryEdeka = new OccupancyHistory(locationEdeka, 10, 0.5, 1);
    super.insert(occupancyHistoryEdeka);

    ZonedDateTime zonedDateTime10 = ZonedDateTime.parse("2020-05-18T10:14:00Z");
    ZonedDateTime zonedDateTime14 = ZonedDateTime.parse("2020-05-18T14:14:00Z");

    Occupancy occupancyEdeka10 = new Occupancy(locationEdeka, 0.1, "TEST", null);
    occupancyEdeka10.setTimestamp(zonedDateTime10);
    super.insert(occupancyEdeka10);
    Occupancy occupancyEdeka10Processed = new Occupancy(locationEdeka, 0.5, "TEST", null);
    occupancyEdeka10Processed.setTimestamp(zonedDateTime10);
    occupancyEdeka10Processed.setHistoryProcessed(true);
    super.insert(occupancyEdeka10Processed);
    Occupancy occupancyAldi10 = new Occupancy(locationAldi, 0.4, "TEST", null);
    occupancyAldi10.setTimestamp(zonedDateTime10);
    super.insert(occupancyAldi10);
    Occupancy occupancyAldi14 = new Occupancy(locationAldi, 0.6, "TEST", null);
    occupancyAldi14.setTimestamp(zonedDateTime14);
    super.insert(occupancyAldi14);

    // Mock
    Mockito.when(occupancyService.getAggregationHour(Mockito.any(), Mockito.any()))
        .then(invocation -> ((ZonedDateTime) invocation.getArgument(0))
            .toInstant().equals(zonedDateTime10.toInstant()) ? 10 : 14);

    // Compute
    occupancyHistoryService.aggregateHistory();

    // Test
    // All Occupancies should be flagged as history processed
    occupancyRepository.findAll()
        .forEach(occupancy -> assertThat(occupancy.isHistoryProcessed()).isTrue());

    List<OccupancyHistory> occupancyHistoryList = occupancyHistoryRepository.findAll();
    // There should be only three different OccupancyHistories
    // Edeka-10, Aldi-10 and Aldi-14
    assertThat(occupancyHistoryList.size()).isEqualTo(3);

    // Pre existing OccupancyHistory
    Optional<OccupancyHistory> resultEdeka10 = occupancyHistoryList.stream()
        .filter(o -> o.getLocation().equals(locationEdeka) && o.getAggregationHour() == 10)
        .findAny();
    assertThat(resultEdeka10.isPresent()).isTrue();
    assertThat(resultEdeka10.get().getOccupancySum()).isEqualTo(0.6);
    assertThat(resultEdeka10.get().getOccupancyCount()).isEqualTo(2);

    Optional<OccupancyHistory> resultAldi10 = occupancyHistoryList.stream()
        .filter(o -> o.getLocation().equals(locationAldi) && o.getAggregationHour() == 10)
        .findAny();
    assertThat(resultAldi10.isPresent()).isTrue();
    assertThat(resultAldi10.get().getOccupancySum()).isEqualTo(0.4);
    assertThat(resultAldi10.get().getOccupancyCount()).isEqualTo(1);

    Optional<OccupancyHistory> resultAldi14 = occupancyHistoryList.stream()
        .filter(o -> o.getLocation().equals(locationAldi) && o.getAggregationHour() == 14)
        .findAny();
    assertThat(resultAldi14.isPresent()).isTrue();
    assertThat(resultAldi14.get().getOccupancySum()).isEqualTo(0.6);
    assertThat(resultAldi14.get().getOccupancyCount()).isEqualTo(1);
  }
}