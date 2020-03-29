package de.sakpaas.backend.v2.mapper;

import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.service.OccupancyService;
import de.sakpaas.backend.v2.dto.LocationResultLocationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {

    private final OccupancyService occupancyService;

    @Autowired
    public LocationMapper(OccupancyService occupancyService) {
        this.occupancyService = occupancyService;
    }

    public LocationResultLocationDto mapToOutputDto(Location location) {
        if (location == null) {
            return null;
        }

        return new LocationResultLocationDto(
                location.getId(), location.getName(),
                new LocationResultLocationDto.LocationResultLocationDetailsDto(location.getDetails()),
                new LocationResultLocationDto.LocationResultCoordinatesDto(location.getLatitude(), location.getLongitude()),
                new LocationResultLocationDto.LocationResultOccupancyDto(occupancyService.getOccupancyCalculation(location)),
                new LocationResultLocationDto.LocationResultAddressDto(location.getAddress()));
    }
}
