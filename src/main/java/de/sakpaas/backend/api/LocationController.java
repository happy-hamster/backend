package de.sakpaas.backend.api;

import de.sakpaas.backend.dto.LocationApiSearchDAS;
import de.sakpaas.backend.dto.LocationSearchOSMResultDto;
import de.sakpaas.backend.dto.LocationSearchOutputDto;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.service.LocationMapper;
import de.sakpaas.backend.service.LocationService;
import javassist.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;
@CrossOrigin
@RequestMapping("api/v1/locations")
@RestController
public class LocationController {
    private final LocationService locationService;
    private final LocationApiSearchDAS locationApiSearchDAS;
    private final LocationMapper locationMapper;

    public LocationController(LocationService locationService, LocationApiSearchDAS locationApiSearchDAS,
            LocationMapper locationMapper) {
        this.locationService = locationService;
        this.locationApiSearchDAS = locationApiSearchDAS;
        this.locationMapper = locationMapper;
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
}
