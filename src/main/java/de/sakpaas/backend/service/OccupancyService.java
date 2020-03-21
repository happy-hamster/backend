package de.sakpaas.backend.service;

import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.Occupancy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.time.ZonedDateTime.now;

@Service
public class OccupancyService {
    private final OccupancyRepository occupancyRepository;

    @Autowired
    public OccupancyService(OccupancyRepository occupancyRepository) {
        this.occupancyRepository = occupancyRepository;
    }

    //TODO: Für Joost :)
    public double getAverageOccupancy(Location location) {
        List<Occupancy> occupancies = occupancyRepository.findByLocationAndTimestampAfter(location,
                now().minusHours(2));

        return 0;
    }
}
