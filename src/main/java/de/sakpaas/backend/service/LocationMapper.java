package de.sakpaas.backend.service;

import de.sakpaas.backend.dto.*;
import de.sakpaas.backend.model.Address;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.LocationDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {

    private final OccupancyService occupancyService;
    private final LocationService locationService;
    private final LocationDetailsService locationDetailsService;
    private final AddressService addressService;

    @Autowired
    public LocationMapper(OccupancyService occupancyService, LocationService locationService,
                          LocationDetailsService locationDetailsService, AddressService addressService) {
        this.occupancyService = occupancyService;
        this.locationService = locationService;
        this.locationDetailsService = locationDetailsService;
        this.addressService = addressService;
    }

    public LocationDto mapToOutputDto(Location location) {
        if (location == null) {
            return null;
        }

        return new LocationDto(
                location.getId(), location.getName(),
                new LocationDetailsDto(location.getDetails()),
                new CoordinatesDto(location.getLatitude(), location.getLongitude()),
                occupancyService.getOccupancyCalculation(location),
                new AddressDto(location.getAddress()));
    }

    public Location mapToLocation(LocationOSMDto apiResult) {
        if (apiResult == null) {
            return null;
        }

        return locationService.getById(apiResult.getId())
                .orElseGet(() -> {
                    LocationDetails details = new LocationDetails(
                            apiResult.getType(),
                            "Mo-Fr 07-22 Uhr, Sa-So 09-12 Uhr"
                    );
                    locationDetailsService.save(details);

                    Address address = new Address(
                            apiResult.getCountry(),
                            apiResult.getCity(),
                            apiResult.getPostcode(),
                            apiResult.getStreet(),
                            apiResult.getHousenumber()
                    );
                    addressService.save(address);

                    Location location = new Location(
                            apiResult.getId(),
                            apiResult.getName() != null ? apiResult.getName() : "Supermarkt",
                            apiResult.getCoordinates().getLatitude(),
                            apiResult.getCoordinates().getLongitude(),
                            details, address
                    );
                    return location;
                });
    }
}
