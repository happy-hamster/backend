package de.sakpaas.backend.service;

import de.sakpaas.backend.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public static double distanceInKm(double lat1, double lon1, double lat2, double lon2) {
        //https://www.daniel-braun.com/technik/distanz-zwischen-zwei-gps-koordinaten-in-java-berchenen/
        int radius = 6371;
        double lat = Math.toRadians(lat2 - lat1);
        double lon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(lat / 2) * Math.sin(lat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(lon / 2) * Math.sin(lon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = radius * c;
        return Math.abs(d);
    }

    public List<Location> findByCoordinates(Double lat, Double lon) {
        List<Location> list = locationRepository.findByLatitudeBetweenAndLongitudeBetween(lat - 0.1, lat + 0.1, lon - 0.1, lon + 0.1);
        return list.stream()
                .sorted(Comparator.comparingDouble(l -> distanceInKm(l.getLatitude(), l.getLongitude(), lat, lon)))
                .limit(100)
                .collect(Collectors.toList());
    }

    public Location save(Location location) {
        return locationRepository.save(location);
    }
}
