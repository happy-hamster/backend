package de.sakpaas.backend.api;

import de.sakpaas.backend.dto.LocationApiSearchDAS;
import de.sakpaas.backend.dto.LocationSearchOSMResultDto;
import de.sakpaas.backend.dto.OccupancyDto;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.Occupancy;
import de.sakpaas.backend.service.LocationMapper;
import de.sakpaas.backend.service.OccupancyService;
import javassist.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RequestMapping("api/v1/occupancies")
@RestController
public class OccupancyController {
    private final LocationMapper locationMapper;
    private final LocationApiSearchDAS locationApiSearchDAS;
    private final OccupancyService occupancyService;

    public OccupancyController(LocationMapper locationMapper, LocationApiSearchDAS locationApiSearchDAS,
            OccupancyService occupancyService) {
        this.locationMapper = locationMapper;
        this.locationApiSearchDAS = locationApiSearchDAS;
        this.occupancyService = occupancyService;
    }

    @PostMapping
    public ResponseEntity<String> postNewOccupancy(@RequestBody OccupancyDto occupancyDto) throws NotFoundException {
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
}
