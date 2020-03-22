package de.sakpaas.backend.api;

import de.sakpaas.backend.BackendApplication;
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

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@CrossOrigin(origins = "*")
@RequestMapping("api/v1/locations")
@RestController
public class LocationController {
    private static final String MAPPING_POST_OCCUPANCY = "/{locationId}/occupancy";
    private static final String MAPPING_POST_CHECKIN = "/{locationId}/check-in";
    private static final String MAPPING_BY_ID = "/{locationId}";
    private static final String MAPPING_START_DATABASE = "/generate/{key}";

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
            @RequestParam Double longitude) throws NotFoundException {
        List<Location> searchResult = locationService.findByCoordinates(latitude, longitude);

        if (searchResult.isEmpty()) {
            throw new NotFoundException("no locations found!");
        }

        List<LocationSearchOutputDto> response = searchResult.stream()
                .map(locationMapper::mapToOutputDto)
                .collect(toList());

        return new ResponseEntity<>(response, OK);
    }

    @GetMapping(value = MAPPING_BY_ID)
    public ResponseEntity<LocationSearchOutputDto> getById(@PathVariable("locationId") Long locationId) {
        Location location = locationService.getById(locationId).orElse(null);

        if (location == null) {
            return ResponseEntity.notFound().build();
        }

        return new ResponseEntity<>(locationMapper.mapToOutputDto(location), OK);
    }

    @PostMapping(value = MAPPING_POST_OCCUPANCY)
    public ResponseEntity<LocationSearchOutputDto> postNewOccupancy(@RequestBody OccupancyDto occupancyDto,
            @PathVariable("locationId") Long locationId) {

        occupancyDto.setLocationId(locationId);

        Location location = locationService.getById(locationId).orElse(null);

        if (location == null) {
            return ResponseEntity.notFound().build();
        }

        occupancyService.save(new Occupancy(location, occupancyDto.getOccupancy(), occupancyDto.getClientType()));

        return new ResponseEntity<>(locationMapper.mapToOutputDto(location), CREATED);
    }

    @PostMapping(value = MAPPING_POST_CHECKIN)
    public ResponseEntity<String> postNewCheckIn(@PathVariable("locationId") Long locationId) {
        Location location = locationService.getById(locationId).orElse(null);

        if (location != null) {
            presenceService.addNewCheckin(location);
            return ResponseEntity.status(CREATED).build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = MAPPING_START_DATABASE)
    public ResponseEntity<String> startDatabase(@PathVariable("key") String key) {
        if (!key.equals(BackendApplication.GENERATED)) {
            return ResponseEntity.badRequest().build();
        }

        System.out.println("started request to API");
        List<LocationSearchOSMResultDto> results = locationApiSearchDAS.getLocationsForCountry("DE");
        System.out.println("got result!");
        for (int i = 0; i < results.size(); i++) {
            try {
                locationService.save(locationMapper.mapToLocation(results.get(i)));
            } catch (Exception ignored) {
            }
            if (i % 100 == 0) {
                System.out.println(((double) i / (double) results.size()) * 100 + "%");
            }
        }
        System.out.println("finished!");

        return ResponseEntity.ok().build();
    }
}
