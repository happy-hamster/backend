package de.sakpaas.backend.service;

import de.sakpaas.backend.dto.LocationSearchOSMResultDto;
import de.sakpaas.backend.dto.LocationSearchOutputDto;
import de.sakpaas.backend.model.Location;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {
    public LocationSearchOutputDto mapToOutputDto(Location location) {
        if (location == null) {
            return null;
        }

        return new LocationSearchOutputDto(location.getId(), location.getName(), location.getOccupancy(),
                location.getLatitude(), location.getLongitude());
    }

    public LocationSearchOutputDto mapToOutputDto(LocationSearchOSMResultDto apiResult) {
        if (apiResult == null) {
            return null;
        }

        return new LocationSearchOutputDto(apiResult.getId(), apiResult.getName(), null, apiResult.getLat(),
                apiResult.getLon());
    }
}
