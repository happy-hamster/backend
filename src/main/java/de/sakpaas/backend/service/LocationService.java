package de.sakpaas.backend.service;

import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.LocationSearchOSMResultList;
import de.sakpaas.backend.dto.LocationSearchDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationService {
    private final LocationSearchDao locationSearchDao;

    @Autowired
    public LocationService(@Qualifier("LocationSearchDAS") LocationSearchDao locationSearchDao) {
        this.locationSearchDao = locationSearchDao;
    }

    //TODO: at first search for location in our db

    public List<Location> getLocationSearchResultsByCoordinates(Double latitude, Double longitude, Double radius) {
        // search for Locations via OSM-API
        LocationSearchOSMResultList searchResult = locationSearchDao.getLocationByCoordinates(latitude, longitude, radius);
        return searchResult.getElements().stream()
                .map(e -> new Location(e.getId(),e.getName(),null,e.getLat(),e.getLon()))
                .collect(Collectors.toList());
    }

}
