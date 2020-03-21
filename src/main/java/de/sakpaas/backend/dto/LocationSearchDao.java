package de.sakpaas.backend.dto;


import de.sakpaas.backend.model.LocationSearchOSMResultList;

public interface LocationSearchDao {
    LocationSearchOSMResultList getLocationByCoordinates(Double latitude, Double longitude, Double radius);
}
