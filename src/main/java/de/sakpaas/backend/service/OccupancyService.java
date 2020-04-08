package de.sakpaas.backend.service;

import static java.time.ZonedDateTime.now;

import de.sakpaas.backend.model.AccumulatedOccupancy;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.Occupancy;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OccupancyService {

  public static final double FACTOR_A = 2 * 20 ^ 2;
  public static final double FACTOR_B = 1.0 / Math.sqrt(2.0 * Math.PI * Math.pow(0.4, 2));

  private final OccupancyRepository occupancyRepository;

  @Value("${app.occupancy.duration}")
  private int confDuration;

  @Autowired
  public OccupancyService(OccupancyRepository occupancyRepository) {
    this.occupancyRepository = occupancyRepository;
  }

  /**
   * Calculates the average occupancy based on the given reports.
   *
   * @param occupancies the occupancies to calculate with
   * @param time        the time to calculate with
   * @return the average occupancy
   */
  public static Double calculateAverage(List<Occupancy> occupancies, ZonedDateTime time) {
    // If there is no occupancy, we can't give an average
    if (occupancies.isEmpty()) {
      return null;
    }

    // After this deadline we have to factor in the bell curve
    ZonedDateTime deadline = time.minusMinutes(15);

    // Collect all occupancies and factors
    double totalOccupancy = 0.0;
    double totalFactor = 0.0;
    for (Occupancy occupancy : occupancies) {
      double factor = 1.0;
      // Calculate factor if necessary
      if (occupancy.getTimestamp().isBefore(deadline)) {
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

  /**
   * Calculates our specific bell curve at position x.
   *
   * @param x the x value
   * @return the y value
   */
  public static double bellCurve(double x) {
    return FACTOR_B * Math.exp(-Math.pow(-x - 15, 2) / FACTOR_A);
  }

  /**
   * Calculates the {@link AccumulatedOccupancy} for a given location.
   *
   * @param location the location to calculate for
   * @return the occupancy report
   */
  public AccumulatedOccupancy getOccupancyCalculation(Location location) {
    ZonedDateTime time = now();
    List<Occupancy> occupancies = occupancyRepository.findByLocationAndTimestampAfter(location,
        now().minusMinutes(confDuration));

    return new AccumulatedOccupancy(
        calculateAverage(occupancies, time),
        occupancies.size(),
        occupancies.stream().map(Occupancy::getTimestamp).max(Comparator.naturalOrder())
            .orElse(null)
    );
  }

  /**
   * Saves an {@link Occupancy} to the database.
   *
   * @param occupancy the occupancy
   * @return the same occupancy
   */
  public Occupancy save(Occupancy occupancy) {
    return occupancyRepository.save(occupancy);
  }
}
