package de.sakpaas.backend.v1.mapper;

import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.service.OccupancyService;
import de.sakpaas.backend.v1.dto.LocationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {

  private final OccupancyService occupancyService;

  @Autowired
  public LocationMapper(OccupancyService occupancyService) {
    this.occupancyService = occupancyService;
  }

  public LocationDto mapToOutputDto(Location location) {
    if (location == null) {
      return null;
    }

    return new LocationDto(
        location.getId(),
        location.getName(),
        location.getAddress().getCountry(),
        location.getAddress().getCity(),
        location.getAddress().getPostcode(),
        location.getAddress().getStreet(),
        location.getAddress().getHousenumber(),
        occupancyService.getOccupancyCalculation(location).getValue(),
        location.getLatitude(),
        location.getLongitude()
    );
  }
}
