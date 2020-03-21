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
    private LocationSearchDao locationApiSearchDao;
    private LocationSearchDao locationDbSearchDao;

    @Autowired
    public LocationService(@Qualifier("LocationApiSearchDAS") LocationSearchDao locationApiSearchDao, @Qualifier("LocationDbSearchDAS") LocationSearchDao locationDbSearchDao) {
        this.locationApiSearchDao = locationApiSearchDao;
        this.locationDbSearchDao =locationDbSearchDao;
    }

    //TODO: at first search for location in our db

    public List<Location> getLocationSearchResultsByCoordinates(Double latitude, Double longitude, Double radius) {
        // search for Locations via OSM-API
        LocationSearchOSMResultList searchResult = locationApiSearchDao.getLocationByCoordinates(latitude, longitude, radius);
        return searchResult.getElements().stream()
                .map(e -> new Location(e.getId(),e.getName(),null,e.getLat(),e.getLon()))
                .collect(Collectors.toList());
    }

}
