package de.sakpaas.backend.service;

import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.Occupancy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.time.ZonedDateTime.now;

@Service
public class OccupancyService {

    public static final double FACTOR_A = 2 * 20 ^ 2;
    public static final double FACTOR_B = 1.0 / Math.sqrt(2.0 * Math.PI * Math.pow(0.4, 2));

    private final OccupancyRepository occupancyRepository;

    @Autowired
    public OccupancyService(OccupancyRepository occupancyRepository) {
        this.occupancyRepository = occupancyRepository;
    }

    public Double getAverageOccupancy(Location location) {
        ZonedDateTime time = now();
        List<Occupancy> occupancies = occupancyRepository.findByLocationAndTimestampAfter(location,
                now().minusHours(2));
        return calculateAverage(occupancies, time);
    }

    public static Double calculateAverage(List<Occupancy> occupancies, ZonedDateTime time) {
        // If there is no occupancy, we can't give an average
        if(occupancies.isEmpty())
            return null;

        // After this deadline we have to factor in the bell curve
        ZonedDateTime deadline = time.minusMinutes(15);

        // Collect all occupancies and factors
        double totalOccupancy = 0.0;
        double totalFactor = 0.0;
        for(Occupancy occupancy : occupancies) {
            double factor = 1.0;
            // Calculate factor if necessary
            if(occupancy.getTimestamp().isBefore(deadline)) {
                double minutes = ChronoUnit.MINUTES.between(time, occupancy.getTimestamp());
                factor = bellCurve(minutes);
            }

            // Collect
            totalOccupancy += factor * occupancy.getOccupancy();
            totalFactor += factor;
        }

        // Convert occupancy > 1.0 to 1.0 <= occupancy <= 0.0
        return totalOccupancy / totalFactor;
    }

    public static double bellCurve(double x) {
        return FACTOR_B * Math.exp(-Math.pow(-x - 15, 2) / FACTOR_A);
    }
}
