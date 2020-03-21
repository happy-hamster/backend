package de.sakpaas.backend.service;

import de.sakpaas.backend.dto.LocationSearchOSMResultDto;
import de.sakpaas.backend.dto.LocationSearchOutputDto;
import de.sakpaas.backend.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {
    private final OccupancyService occupancyService;

    @Autowired
    public LocationMapper(OccupancyService occupancyService) {
        this.occupancyService = occupancyService;
    }

    public LocationSearchOutputDto mapToOutputDto(Location location) {
        if (location == null) {
            return null;
        }

        return new LocationSearchOutputDto(location.getId(), location.getName(),
                occupancyService.getAverageOccupancy(location), location.getLatitude(), location.getLongitude(), location.getStreet(),
                location.getHousenumber(), location.getPostcode(), location.getCity(), location.getCountry());
    }

    public LocationSearchOutputDto mapToOutputDto(LocationSearchOSMResultDto apiResult) {
        if (apiResult == null) {
            return null;
        }

        return new LocationSearchOutputDto(apiResult.getId(), apiResult.getName(), null, apiResult.getLat(),
                apiResult.getLon(), apiResult.getStreet(), apiResult.getHousenumber(), apiResult.getPostcode(),
                apiResult.getCity(), apiResult.getCountry());
    }
}
