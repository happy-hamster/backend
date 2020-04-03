package de.sakpaas.backend.v2.controller;

import com.google.common.util.concurrent.AtomicDouble;
import de.sakpaas.backend.BackendApplication;
import de.sakpaas.backend.dto.OSMResultLocationListDto;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.Occupancy;
import de.sakpaas.backend.service.LocationApiSearchDAS;
import de.sakpaas.backend.service.LocationService;
import de.sakpaas.backend.service.OccupancyService;
import de.sakpaas.backend.service.PresenceService;
import de.sakpaas.backend.v2.dto.LocationResultLocationDto;
import de.sakpaas.backend.v2.dto.OccupancyReportDto;
import de.sakpaas.backend.v2.mapper.LocationMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@CrossOrigin(origins = "*")
@RequestMapping("/v2/locations")
@RestController
public class LocationController {
    private static final String MAPPING_POST_OCCUPANCY = "/{locationId}/occupancy";
    private static final String MAPPING_POST_CHECKIN = "/{locationId}/check-in";
    private static final String MAPPING_BY_ID = "/{locationId}";
    private static final String MAPPING_START_DATABASE = "/generate/{key}";

    private LocationService locationService;

    private LocationMapper locationMapper;
    private OccupancyService occupancyService;
    private PresenceService presenceService;
    private final MeterRegistry meterRegistry;
    private AtomicBoolean importState;

    private Counter getCounter;
    private Counter getByIdCounter;
    private Counter postOccupancyCounter;
    private Counter postCheckInCounter;
    private Counter getStartDatabaseCounter;


    public LocationController(LocationService locationService,
                              LocationMapper locationMapper, OccupancyService occupancyService, PresenceService presenceService, MeterRegistry meterRegistry) {
        this.locationService = locationService;
        this.locationMapper = locationMapper;
        this.occupancyService = occupancyService;
        this.presenceService = presenceService;
        this.meterRegistry = meterRegistry;
        this.importState = new AtomicBoolean(false);

        getCounter = Counter
            .builder("request")
            .description("Total Request since application start on a Endpoint")
            .tags("version", "v2", "endpoint", "location", "method", "get")
            .register(meterRegistry);
        getByIdCounter = Counter
            .builder("request")
            .description("Total Request since application start on a Endpoint")
            .tags("version", "v2", "endpoint", "location", "method", "getById")
            .register(meterRegistry);
        postOccupancyCounter = Counter
            .builder("request")
            .description("Total Request since application start on a Endpoint")
            .tags("version", "v2", "endpoint", "location", "method", "postOccupancy")
            .register(meterRegistry);
        postCheckInCounter = Counter
            .builder("request")
            .description("Total Request since application start on a Endpoint")
            .tags("version", "v2", "endpoint", "location", "method", "postCheckIn")
            .register(meterRegistry);
        getStartDatabaseCounter = Counter
            .builder("request")
            .description("Total Request since application start on a Endpoint")
            .tags("version", "v2", "endpoint", "location", "method", "getStartDatabase")
            .register(meterRegistry);

    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<LocationResultLocationDto>> getLocation(@RequestParam Double latitude,
                                                                       @RequestParam Double longitude) {
        getCounter.increment();
        List<Location> searchResult = locationService.findByCoordinates(latitude, longitude);

        if (searchResult.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), OK);
        }

        List<LocationResultLocationDto> response = searchResult.stream()
                .map(locationMapper::mapToOutputDto)
                .collect(toList());

        return new ResponseEntity<>(response, OK);
    }

    @GetMapping(value = MAPPING_BY_ID)
    public ResponseEntity<LocationResultLocationDto> getById(@PathVariable("locationId") Long locationId) {
        getByIdCounter.increment();
        Location location = locationService.getById(locationId).orElse(null);

        if (location == null) {
            return ResponseEntity.notFound().build();
        }

        return new ResponseEntity<>(locationMapper.mapToOutputDto(location), OK);
    }

    @PostMapping(value = MAPPING_POST_OCCUPANCY)
    public ResponseEntity<LocationResultLocationDto> postNewOccupancy(@RequestBody OccupancyReportDto occupancyReportDto,
                                                                      @PathVariable("locationId") Long locationId) {
        postOccupancyCounter.increment();

        occupancyReportDto.setLocationId(locationId);

        Location location = locationService.getById(locationId).orElse(null);

        if (location == null) {
            return ResponseEntity.notFound().build();
        }

        occupancyService.save(new Occupancy(location, occupancyReportDto.getOccupancy(), occupancyReportDto.getClientType()));

        return new ResponseEntity<>(locationMapper.mapToOutputDto(location), CREATED);
    }

    @PostMapping(value = MAPPING_POST_CHECKIN)
    public ResponseEntity<String> postNewCheckIn(@PathVariable("locationId") Long locationId) {
        postCheckInCounter.increment();
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
        getStartDatabaseCounter.increment();
        if (!key.equals(BackendApplication.GENERATED)) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Permission denied");

        }
        if(importState.get()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Already running");
        }
        // Lock database import
        importState.set(true);
        // Making the Database import
        locationService.updateDatabase();
        // Unlock database import
        importState.set(false);

        return ResponseEntity.ok("Success");
    }
}
