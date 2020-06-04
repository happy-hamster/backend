package de.sakpaas.backend.service;

import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.Occupancy;
import de.sakpaas.backend.model.OccupancyHistory;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

/**
 * This service is for aggregating Occupancy as historical data.
 */
@Service
@Slf4j
public class OccupancyHistoryService {

  private final OccupancyRepository occupancyRepository;
  private final OccupancyHistoryRepository occupancyHistoryRepository;
  private final OccupancyService occupancyService;

  @Value("${app.history.batch-size}")
  private int batchSize;

  /**
   * Default Constructor. Handles the Dependency Injection.
   *
   * @param occupancyRepository        the {@link OccupancyRepository}
   * @param occupancyHistoryRepository the {@link OccupancyHistoryRepository}
   * @param occupancyService           the {@link OccupancyService}
   */
  @Autowired
  public OccupancyHistoryService(OccupancyRepository occupancyRepository,
                                 OccupancyHistoryRepository occupancyHistoryRepository,
                                 OccupancyService occupancyService) {
    this.occupancyRepository = occupancyRepository;
    this.occupancyHistoryRepository = occupancyHistoryRepository;
    this.occupancyService = occupancyService;
  }


  /**
   * Runs the history aggregation.
   */
  public void aggregateHistory() {
    log.warn("Starting aggregation of Occupancies...");

    int index = 0;
    Slice<Occupancy> occupancies;
    do {
      // Get all not processed Occupancies
      occupancies = occupancyRepository.findByHistoryProcessed(
          false, PageRequest.of(0, batchSize));
      log.info("Aggregation batch #{} with {} items", ++index, occupancies.getNumberOfElements());

      // Get all known OccupancyHistories for the Locations
      Set<Location> locations = occupancies.stream()
          .map(Occupancy::getLocation)
          .collect(Collectors.toSet());
      Set<OccupancyHistory> occupancyHistories =
          occupancyHistoryRepository.findByLocationIn(locations);

      // Update or create OccupancyHistory
      occupancies.forEach(occupancy ->
          findOccupancyHistory(occupancyHistories, occupancy).increment(occupancy.getOccupancy()));
      // Save OccupancyHistory
      occupancyHistoryRepository.saveAll(occupancyHistories);

      // Set the history processed flag
      occupancies.forEach(occupancy -> occupancy.setHistoryProcessed(true));
      occupancyRepository.saveAll(occupancies);
    } while (occupancies.hasNext());

    log.info("Finished aggregation of Occupancies!");
  }

  private OccupancyHistory findOccupancyHistory(Set<OccupancyHistory> occupancyHistories,
                                                Occupancy occupancy) {
    int aggregationHour = occupancyService
        .getAggregationHour(occupancy.getTimestamp(), occupancy.getLocation());
    Optional<OccupancyHistory> occupancyHistoryOptional = occupancyHistories.stream()
        .filter(occupancyHistory -> occupancyHistory.getAggregationHour() == aggregationHour
            && occupancyHistory.getLocation().getId().equals(occupancy.getLocation().getId()))
        .findAny();

    return occupancyHistoryOptional.orElseGet(() -> {
      OccupancyHistory occupancyHistory =
          new OccupancyHistory(occupancy.getLocation(), aggregationHour);
      occupancyHistories.add(occupancyHistory);
      return occupancyHistory;
    });
  }

  /**
   * Returns the OccupancyHistory related to the AggregationHours and Location
   *
   * @param aggregationHours The AggregationHours
   * @param location         The Location
   * @return The OccupancyHistory
   */
  private Set<OccupancyHistory> getByAggregationHour(List<Integer> aggregationHours,
                                                     Location location) {
    Set<OccupancyHistory> resultSet = new HashSet<>();
    for (Integer aggregationHour : aggregationHours) {
      resultSet.addAll(
          occupancyHistoryRepository.findByLocationAndAggregationHour(location, aggregationHour));
    }
    return resultSet;
  }
}


