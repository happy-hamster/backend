package de.sakpaas.backend;

import de.sakpaas.backend.service.AddressRepository;
import de.sakpaas.backend.service.FavoriteRepository;
import de.sakpaas.backend.service.LocationDetailsRepository;
import de.sakpaas.backend.service.LocationRepository;
import de.sakpaas.backend.service.OccupancyRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

public class RepositoryTest extends HappyHamsterTest {

  @Autowired
  protected OccupancyRepository occupancyRepository;
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
    favoriteRepository.deleteAll();
    locationRepository.deleteAll();
    addressRepository.deleteAll();
    locationDetailsRepository.deleteAll();
  }
}
