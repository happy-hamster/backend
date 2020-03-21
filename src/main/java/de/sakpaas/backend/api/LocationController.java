package de.sakpaas.backend.api;

import de.sakpaas.backend.dto.LocationApiSearchDAS;
import de.sakpaas.backend.dto.LocationSearchOSMResultDto;
import de.sakpaas.backend.dto.LocationSearchOutputDto;
import de.sakpaas.backend.dto.OccupancyDto;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.Occupancy;
import de.sakpaas.backend.service.LocationMapper;
import de.sakpaas.backend.service.LocationService;
import de.sakpaas.backend.service.OccupancyService;
import de.sakpaas.backend.service.PresenceService;
import javassist.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@CrossOrigin(origins = "*")
@RequestMapping("api/v1/locations")
@RestController
public class LocationController {
    private static final String MAPPING_POST_OCCUPANCY = "/{locationId}/occupancy";
    private static final String MAPPING_POST_CHECKIN = "/{locationId}/check-in";
    private static final String MAPPING_BY_ID = "/{locationId}";

    private LocationService locationService;
    private LocationApiSearchDAS locationApiSearchDAS;
    private LocationMapper locationMapper;
    private OccupancyService occupancyService;
    private PresenceService presenceService;

    public LocationController(LocationService locationService, LocationApiSearchDAS locationApiSearchDAS,
            LocationMapper locationMapper, OccupancyService occupancyService, PresenceService presenceService) {
        this.locationService = locationService;
        this.locationApiSearchDAS = locationApiSearchDAS;
        this.locationMapper = locationMapper;
        this.occupancyService = occupancyService;
        this.presenceService = presenceService;
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<LocationSearchOutputDto>> getLocation(@RequestParam Double latitude,
            @RequestParam Double longitude, @RequestParam(required = false) Double radius) throws NotFoundException {
        if (radius == null) {
            radius = 5000.0;
        }

        List<LocationSearchOSMResultDto> apiResultLocationList = locationApiSearchDAS.getLocationByCoordinates(
                latitude, longitude, radius);

        if (apiResultLocationList == null) {
            throw new NotFoundException("no locations found!");
        }

        List<LocationSearchOutputDto> response = new ArrayList<>();
        apiResultLocationList.forEach(location -> {
            Location existingLocation = locationService.getById(location.getId()).orElse(null);
            if (existingLocation != null) {
                response.add(locationMapper.mapToOutputDto(existingLocation));
            } else {
                response.add(locationMapper.mapToOutputDto(location));
            }
        });

        return new ResponseEntity<>(response, OK);
    }

    @GetMapping(value = MAPPING_BY_ID)
    public ResponseEntity<LocationSearchOutputDto> getById(@PathVariable("locationId") Long locationId) {
        Location location = locationService.getById(locationId).orElse(null);

        if (location == null) {
            return new ResponseEntity<>(locationMapper.mapToOutputDto(locationApiSearchDAS.getLocationById(locationId)),
                    OK);
        }

        return new ResponseEntity<>(locationMapper.mapToOutputDto(location), OK);
    }

    @PostMapping(value = MAPPING_POST_OCCUPANCY)
    public ResponseEntity<String> postNewOccupancy(@RequestBody OccupancyDto occupancyDto,
            @PathVariable("locationId") Long locationId) throws NotFoundException {

        occupancyDto.setLocationId(locationId);
        LocationSearchOSMResultDto locationSearchOSMResultDto = locationApiSearchDAS.getLocationById(
                occupancyDto.getLocationId());

        if (locationSearchOSMResultDto == null) {
            throw new NotFoundException("No Location with id: " + occupancyDto.getLocationId() + " found");
        }

        Location location = locationMapper.mapToLocation(
                locationApiSearchDAS.getLocationById(occupancyDto.getLocationId()));

        occupancyService.save(new Occupancy(location, occupancyDto.getOccupancy()));

        return new ResponseEntity<>("Success!", CREATED);
    }

    @PostMapping(value = MAPPING_POST_CHECKIN)
    public ResponseEntity<String> postNewCheckIn(@PathVariable("locationId") Long locationId) throws NotFoundException {
        Location location = locationService.getById(locationId).orElse(null);

        if (location == null) {
            location = locationService.save(
                    locationMapper.mapToLocation(locationApiSearchDAS.getLocationById(locationId)));
        }

        if (location != null) {
            presenceService.addNewCheckin(location);
            return new ResponseEntity<>("Success!", CREATED);
        } else {
            throw new NotFoundException("Found no location to id: " + locationId);
        }

    }
}
