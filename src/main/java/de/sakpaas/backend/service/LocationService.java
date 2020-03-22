package de.sakpaas.backend.service;

import de.sakpaas.backend.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocationService {
    private LocationRepository locationRepository;

    @Autowired
    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    //TODO: at first search for location in our db

    public Optional<Location> getById(long id) {
        return locationRepository.findById(id);
    }

    public List<Location> findByCoordinates(Double lat, Double lon) {
        return locationRepository.findByLatitudeBetweenAndLongitudeBetween(lat - 0.1, lat + 0.1, lon - 0.1, lon + 0.1);
    }

    public Location save(Location location) {
        return locationRepository.save(location);
    }
}
