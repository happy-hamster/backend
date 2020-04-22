package de.sakpaas.backend.service;

import com.google.common.annotations.VisibleForTesting;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.util.CoordinatesUtils;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LocationService {

  private final LocationRepository locationRepository;
  private final PresenceRepository presenceRepository;
  private final OccupancyRepository occupancyRepository;
  private final FavoriteService favoriteService;

  /**
   * Default Constructor. Handles the Dependency Injection and Meter Initialisation and Registering
   *
   * @param locationRepository The Location Repository
   */
  @Autowired
  public LocationService(LocationRepository locationRepository,
                         PresenceRepository presenceRepository,
                         OccupancyRepository occupancyRepository,
                         FavoriteService favoriteService) {
    this.locationRepository = locationRepository;
    this.presenceRepository = presenceRepository;
    this.occupancyRepository = occupancyRepository;
    this.favoriteService = favoriteService;
  }

  /**
   * Gets a Location by its ID from the Database.
   *
   * @param id Id of the requested location
   * @return Location from the Database
   */
  public Optional<Location> getById(long id) {
    return locationRepository.findById(id);
  }

  /**
   * Gets all Locations from a specific coordinate.
   *
   * @param lat Latitude of the Location.
   * @param lon Longitude of the Location.
   * @return List of max 100 Locations around the given coordinates.
   */
  public List<Location> findByCoordinates(Double lat, Double lon) {
    List<Location> list = locationRepository
        .findByLatitudeBetweenAndLongitudeBetween(lat - 0.1, lat + 0.1, lon - 0.1, lon + 0.1);
    return list.stream()
        .sorted(Comparator
            .comparingDouble(
                l -> CoordinatesUtils.distanceInKm(l.getLatitude(), l.getLongitude(), lat, lon)))
        .limit(100)
        .collect(Collectors.toList());
  }

  /**
   * Saves a Location to the Database.
   *
   * @param location Location that will be saved
   */
  public Location save(Location location) {
    return locationRepository.save(location);
  }

  /**
   * Deletes the given Location and all depending entities.
   *
   * @param location Location that needs to be deleted
   */
  @VisibleForTesting
  protected void delete(Location location) {
    occupancyRepository.findByLocation(location).forEach(occupancyRepository::delete);
    presenceRepository.findByLocation(location).forEach(presenceRepository::delete);
    favoriteService.deleteByLocation(location);
    locationRepository.delete(location);
  }
}
