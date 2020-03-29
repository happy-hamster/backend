package de.sakpaas.backend.v2.mapper;

import de.sakpaas.backend.dto.OSMResultLocationListDto;
import de.sakpaas.backend.model.Address;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.LocationDetails;
import de.sakpaas.backend.service.AddressService;
import de.sakpaas.backend.service.LocationDetailsService;
import de.sakpaas.backend.service.LocationService;
import de.sakpaas.backend.service.OccupancyService;
import de.sakpaas.backend.v2.dto.LocationResultLocationDto;
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
                new LocationResultLocationDto.LocationResultLocationDetailsDto(location.getDetails()),
                new LocationResultLocationDto.LocationResultCoordinatesDto(location.getLatitude(), location.getLongitude()),
                new LocationResultLocationDto.LocationResultOccupancyDto(occupancyService.getOccupancyCalculation(location)),
                new LocationResultLocationDto.LocationResultAddressDto(location.getAddress()));
    }

    public Location mapToLocation(OSMResultLocationListDto.OMSResultLocationDto apiResult) {
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
                            apiResult.getCoordinates().getLat(),
                            apiResult.getCoordinates().getLon(),
                            details, address
                    );
                });
    }
}
