package de.sakpaas.backend.service;

import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.OccupancyHistory;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OccupancyHistoryRepository extends JpaRepository<OccupancyHistory, Long> {

  /**
   * Returns all {@link OccupancyHistory}s for a given {@link List} of {@link Location}.
   *
   * @param locations the {@link List} of {@link Location} to search for
   * @return {@link List} of {@link OccupancyHistory}s
   */
  Set<OccupancyHistory> findByLocationIn(Set<Location> locations);

}
