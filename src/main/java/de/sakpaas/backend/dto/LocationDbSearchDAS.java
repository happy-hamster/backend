package de.sakpaas.backend.dto;

import de.sakpaas.backend.model.LocationSearchOSMResultList;
import org.springframework.stereotype.Repository;

@Repository("LocationDbSearchDAS")
public class LocationDbSearchDAS implements LocationSearchDao {
    @Override
    public LocationSearchOSMResultList getLocationByCoordinates(Double latitude, Double longitude, Double radius) {
        return null;
    }
}
