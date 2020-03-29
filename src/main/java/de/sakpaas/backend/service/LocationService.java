package de.sakpaas.backend.service;

import de.sakpaas.backend.dto.OSMResultLocationListDto;
import de.sakpaas.backend.model.Address;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.LocationDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LocationService {
    private final LocationRepository locationRepository;
    private final LocationDetailsService locationDetailsService;
    private final AddressService addressService;

    @Autowired
    public LocationService(LocationRepository locationRepository,
                           LocationDetailsService locationDetailsService,
                           AddressService addressService) {
        this.locationRepository = locationRepository;
        this.locationDetailsService = locationDetailsService;
        this.addressService = addressService;
    }

    public Optional<Location> getById(long id) {
        return locationRepository.findById(id);
    }

    public static double distanceInKm(double lat1, double lon1, double lat2, double lon2) {
        // https://www.daniel-braun.com/technik/distanz-zwischen-zwei-gps-koordinaten-in-java-berchenen/
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

    /**
     * Inserts or updates an OSM-Location in the database.
     *
     * @param osmLocation the OSM-Location to be inserted or updated
     */
    public void importLocation(OSMResultLocationListDto.OMSResultLocationDto osmLocation) {
        Optional<Location> optionalLocation = locationRepository.findById(osmLocation.getId());

        if (optionalLocation.isPresent()) {
            Location location = optionalLocation.get();

            LocationDetails details = location.getDetails();
            details.setType(osmLocation.getType());
            details.setOpeningHours(osmLocation.getOpeningHours());
            details.setBrand(osmLocation.getBrand());
            locationDetailsService.save(details);

            locationDetailsService.save(details);
            Address address = location.getAddress();
            address.setCountry(osmLocation.getCountry());
            address.setCity(osmLocation.getCity());
            address.setPostcode(osmLocation.getPostcode());
            address.setStreet(osmLocation.getStreet());
            address.setHousenumber(osmLocation.getHousenumber());
            addressService.save(address);

            location.setName(osmLocation.getName() != null ? osmLocation.getName() : "Supermarkt");
            location.setLatitude(osmLocation.getCoordinates().getLat());
            location.setLongitude(osmLocation.getCoordinates().getLon());
            this.save(location);
        } else {
            LocationDetails details = new LocationDetails(
                    osmLocation.getType(),
                    osmLocation.getOpeningHours(),
                    osmLocation.getBrand()
            );
            locationDetailsService.save(details);

            Address address = new Address(
                    osmLocation.getCountry(),
                    osmLocation.getCity(),
                    osmLocation.getPostcode(),
                    osmLocation.getStreet(),
                    osmLocation.getHousenumber()
            );
            addressService.save(address);

            Location location = new Location(
                    osmLocation.getId(),
                    osmLocation.getName() != null ? osmLocation.getName() : "Supermarkt",
                    osmLocation.getCoordinates().getLat(),
                    osmLocation.getCoordinates().getLon(),
                    details,
                    address
            );
            this.save(location);
        }
    }
}
