package de.sakpaas.backend.api;

import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.service.LocationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/v1/locations")
@RestController
public class LocationController {
    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    @ResponseBody
    public List<Location> getLocation(@RequestParam Double latitude, @RequestParam Double longitude, @RequestParam(required = false) Double radius){
        if (radius==null){radius = 5000.0;}
        return locationService.getLocationSearchResultsByCoordinates(latitude,longitude,radius);
    }
}
