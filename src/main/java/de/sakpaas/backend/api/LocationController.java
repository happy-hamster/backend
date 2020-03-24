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
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@CrossOrigin(origins = "*")
@RequestMapping("/v1/locations")
@RestController
public class LocationController {
    private static final String MAPPING_POST_OCCUPANCY = "/{locationId}/occupancy";
    private static final String MAPPING_BY_ID = "/{locationId}";
    private static final String MAPPING_START_DATABASE = "/generate/{key}";


    private LocationService locationService;
    private LocationApiSearchDAS locationApiSearchDAS;
    private LocationMapper locationMapper;
    private OccupancyService occupancyService;
    private final MeterRegistry meterRegistry;

    private Counter getCounter;
    private Counter getByIdCounter;
    private Counter postOccupancyCounter;
    private Counter getStartDatabaseCounter;

    public LocationController(LocationService locationService, LocationApiSearchDAS locationApiSearchDAS,
                              LocationMapper locationMapper, OccupancyService occupancyService, MeterRegistry meterRegistry) {
        this.locationService = locationService;
        this.locationApiSearchDAS = locationApiSearchDAS;
        this.locationMapper = locationMapper;
        this.occupancyService = occupancyService;
        this.meterRegistry = meterRegistry;

        getCounter = Counter
            .builder("request")
            .description("Total Request since application start on a Endpoint")
            .tags("endpoint", "location", "method", "get")
            .register(meterRegistry);
        getByIdCounter = Counter
            .builder("request")
            .description("Total Request since application start on a Endpoint")
            .tags("endpoint", "location", "method", "getById")
            .register(meterRegistry);
        postOccupancyCounter = Counter
            .builder("request")
            .description("Total Request since application start on a Endpoint")
            .tags("endpoint", "location", "method", "postOccupancy")
            .register(meterRegistry);
        getStartDatabaseCounter = Counter
            .builder("request")
            .description("Total Request since application start on a Endpoint")
            .tags("endpoint", "location", "method", "getStartDatabase")
            .register(meterRegistry);
    }



    @GetMapping
    @ResponseBody
    public ResponseEntity<List<LocationSearchOutputDto>> getLocation(@RequestParam Double latitude,
            @RequestParam Double longitude) {
        getCounter.increment();
        List<Location> searchResult = locationService.findByCoordinates(latitude, longitude);

        if (searchResult.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(),OK);
        }

        List<LocationSearchOutputDto> response = searchResult.stream()
                .map(locationMapper::mapToOutputDto)
                .collect(toList());

        return new ResponseEntity<>(response, OK);
    }

    @GetMapping(value = MAPPING_BY_ID)
    public ResponseEntity<LocationSearchOutputDto> getById(@PathVariable("locationId") Long locationId) {
        getByIdCounter.increment();
        Location location = locationService.getById(locationId).orElse(null);

        if (location == null) {
            return ResponseEntity.notFound().build();
        }

        return new ResponseEntity<>(locationMapper.mapToOutputDto(location), OK);
    }

    @PostMapping(value = MAPPING_POST_OCCUPANCY)
    public ResponseEntity<LocationSearchOutputDto> postNewOccupancy(@RequestBody OccupancyDto occupancyDto,
            @PathVariable("locationId") Long locationId) {
        postOccupancyCounter.increment();

        occupancyDto.setLocationId(locationId);

        Location location = locationService.getById(locationId).orElse(null);

        if (location == null) {
            return ResponseEntity.notFound().build();
        }

        occupancyService.save(new Occupancy(location, occupancyDto.getOccupancy(), occupancyDto.getClientType()));

        return new ResponseEntity<>(locationMapper.mapToOutputDto(location), CREATED);
    }

    @GetMapping(value = MAPPING_START_DATABASE)
    public ResponseEntity<String> startDatabase(@PathVariable("key") String key) {
        getStartDatabaseCounter.increment();
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
