package de.sakpaas.backend.v2.mapper;

import de.sakpaas.backend.dto.UserInfoDto;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.service.FavoriteRepository;
import de.sakpaas.backend.service.OccupancyService;
import de.sakpaas.backend.v2.dto.LocationResultLocationDto;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {

  private final OccupancyService occupancyService;
  private final FavoriteRepository favoriteRepository;

  @Autowired
  public LocationMapper(OccupancyService occupancyService, FavoriteRepository favoriteRepository) {
    this.occupancyService = occupancyService;
    this.favoriteRepository = favoriteRepository;
  }

  /**
   * Maps the given Location to a v2 LocationDto.
   *
   * @param location the Location to be mapped
   * @return the mapped LocationDto
   */
  public LocationResultLocationDto mapLocationToOutputDto(Location location) {
    if (location == null) {
      return null;
    }

    return new LocationResultLocationDto(
        location.getId(), location.getName(), false,
        new LocationResultLocationDto.LocationResultLocationDetailsDto(location.getDetails()),
        new LocationResultLocationDto.LocationResultCoordinatesDto(location.getLatitude(),
            location.getLongitude()),
        new LocationResultLocationDto.LocationResultOccupancyDto(
            occupancyService.getOccupancyCalculation(location)),
        new LocationResultLocationDto.LocationResultAddressDto(location.getAddress()));
  }

  /**
   * Maps the given Location to a v2 LocationDto with favorite flag.
   *
   * @param location the Location to be mapped
   * @param user     the user for which the flags should be set
   * @return the mapped LocationDto
   */
  public LocationResultLocationDto mapLocationToOutputDto(Location location, UserInfoDto user) {
    if (location == null) {
      return null;
    }

    boolean flag = favoriteRepository.findByUserUuid(UUID.fromString(user.getId()))
        .stream()
        .anyMatch(favorite -> favorite.getLocation() == location);

    return new LocationResultLocationDto(
        location.getId(), location.getName(), flag,
        new LocationResultLocationDto.LocationResultLocationDetailsDto(location.getDetails()),
        new LocationResultLocationDto.LocationResultCoordinatesDto(location.getLatitude(),
            location.getLongitude()),
        new LocationResultLocationDto.LocationResultOccupancyDto(
            occupancyService.getOccupancyCalculation(location)),
        new LocationResultLocationDto.LocationResultAddressDto(location.getAddress()));
  }
}
