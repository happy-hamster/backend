package de.sakpaas.backend;

import de.sakpaas.backend.model.Favorite;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.Occupancy;
import de.sakpaas.backend.model.OccupancyHistory;
import de.sakpaas.backend.service.AddressRepository;
import de.sakpaas.backend.service.FavoriteRepository;
import de.sakpaas.backend.service.LocationDetailsRepository;
import de.sakpaas.backend.service.LocationRepository;
import de.sakpaas.backend.service.OccupancyHistoryRepository;
import de.sakpaas.backend.service.OccupancyRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

public class RepositoryTest extends HappyHamsterTest {

  @Autowired
  protected OccupancyRepository occupancyRepository;
  @Autowired
  protected OccupancyHistoryRepository occupancyHistoryRepository;
  @Autowired
  protected FavoriteRepository favoriteRepository;
  @Autowired
  protected LocationRepository locationRepository;
  @Autowired
  protected LocationDetailsRepository locationDetailsRepository;
  @Autowired
  protected AddressRepository addressRepository;

  @BeforeEach
  @AfterEach
  protected void clearTables() {
    occupancyRepository.deleteAll();
    occupancyHistoryRepository.deleteAll();
    favoriteRepository.deleteAll();
    locationRepository.deleteAll();
    addressRepository.deleteAll();
    locationDetailsRepository.deleteAll();
  }

  protected void insert(Location location) {
    locationDetailsRepository.save(location.getDetails());
    addressRepository.save(location.getAddress());
    locationRepository.save(location);
  }

  protected void insert(Favorite favorite) {
    favoriteRepository.save(favorite);
  }

  protected void insert(Occupancy occupancy) {
    occupancyRepository.save(occupancy);
  }

  protected void insert(OccupancyHistory occupancyHistory) {
    occupancyHistoryRepository.save(occupancyHistory);
  }
}
