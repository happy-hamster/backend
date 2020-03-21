package de.sakpaas.backend.service;

import de.sakpaas.backend.dto.LocationSearchOSMResultDto;
import de.sakpaas.backend.dto.LocationSearchOutputDto;
import de.sakpaas.backend.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {
    private final OccupancyService occupancyService;
    private final LocationService locationService;

    @Autowired
    public LocationMapper(OccupancyService occupancyService, LocationService locationService) {
        this.occupancyService = occupancyService;
        this.locationService = locationService;
    }

    public LocationSearchOutputDto mapToOutputDto(Location location) {
        if (location == null) {
            return null;
        }

        return new LocationSearchOutputDto(location.getId(), location.getName(),
                occupancyService.getAverageOccupancy(location), location.getLatitude(), location.getLongitude());
    }

    public LocationSearchOutputDto mapToOutputDto(LocationSearchOSMResultDto apiResult) {
        if (apiResult == null) {
            return null;
        }

        return new LocationSearchOutputDto(apiResult.getId(), apiResult.getName(), null, apiResult.getLat(),
                apiResult.getLon());
    }

    public Location mapToLocation(LocationSearchOSMResultDto apiResult) {
        if (apiResult == null) {
            return null;
        }

        return locationService.getById(apiResult.getId())
                .orElseGet(() -> locationService.save(
                        new Location(apiResult.getId(), apiResult.getName(), apiResult.getLat(),
                                apiResult.getLon())));
    }
}
