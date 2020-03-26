package de.sakpaas.backend.service;

import de.sakpaas.backend.dto.locationresult.LocationResultAddressDto;
import de.sakpaas.backend.dto.locationresult.LocationResultCoordinatesDto;
import de.sakpaas.backend.dto.locationresult.LocationResultLocationDetailsDto;
import de.sakpaas.backend.dto.locationresult.LocationResultLocationDto;
import de.sakpaas.backend.dto.osmresult.OMSResultLocationDto;
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

    public LocationResultLocationDto mapToOutputDto(Location location) {
        if (location == null) {
            return null;
        }

        return new LocationResultLocationDto(
                location.getId(), location.getName(),
                new LocationResultLocationDetailsDto(location.getDetails()),
                new LocationResultCoordinatesDto(location.getLatitude(), location.getLongitude()),
                occupancyService.getOccupancyCalculation(location),
                new LocationResultAddressDto(location.getAddress()));
    }

    public Location mapToLocation(OMSResultLocationDto apiResult) {
        if (apiResult == null) {
            return null;
        }

        return locationService.getById(apiResult.getId())
                .orElseGet(() -> {
                    LocationDetails details = new LocationDetails(
                            apiResult.getType(),
                            apiResult.getOpeningHours(),
                            apiResult.getBrand()
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

                    return new Location(
                            apiResult.getId(),
                            apiResult.getName() != null ? apiResult.getName() : "Supermarkt",
                            apiResult.getCoordinates().getLatitude(),
                            apiResult.getCoordinates().getLongitude(),
                            details, address
                    );
                });
    }
}
