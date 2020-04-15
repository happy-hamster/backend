package de.sakpaas.backend.service;

import static java.time.ZonedDateTime.now;

import com.google.common.annotations.VisibleForTesting;
import de.sakpaas.backend.model.AccumulatedOccupancy;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.Occupancy;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Getter
@Service
public class OccupancyService {

  @Getter(AccessLevel.PRIVATE)
  private final OccupancyRepository occupancyRepository;

  @Value("${app.occupancy.duration}")
  private int configDuration;
  @Value("${app.occupancy.constant}")
  private int configConstant;

  @Value("${app.occupancy.minimum}")
  private double configMinimum;
  @Value("${app.occupancy.factorA}")
  private double configFactorA;
  @Value("${app.occupancy.factorB}")
  private double configFactorB;

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
  private Double calculateAverage(List<Occupancy> occupancies, ZonedDateTime time) {
    // If there is no occupancy, we can't give an average
    if (occupancies.isEmpty()) {
      return null;
    }

    // Collect all occupancies and factors
    double totalOccupancy = 0.0;
    double totalFactor = 0.0;
    for (Occupancy occupancy : occupancies) {
      // Calculate curve
      double minutes = ChronoUnit.MINUTES.between(time, occupancy.getTimestamp());
      double factor = calculateFactor(minutes);

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
  @VisibleForTesting
  double calculateFactor(double x) {
    // See documentation for a more understandable formula
    double base = 1.0 + (1.0 / this.getConfigFactorA());
    double exponent = -Math.pow(-x - this.getConfigConstant(), 2) / this.getConfigFactorB();
    return (x < -this.getConfigConstant())
        ? (1.0 - this.getConfigMinimum()) * Math.pow(base, exponent) + this.getConfigMinimum()
        : 1;
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
        now().minusMinutes(this.getConfigDuration()));

    return new AccumulatedOccupancy(
        calculateAverage(occupancies, time),
        occupancies.size(),
        occupancies.stream()
            .map(Occupancy::getTimestamp)
            .max(Comparator.naturalOrder())
            .orElse(null)
    );
  }

  /**
   * Saves an {@link Occupancy} to the database.
   *
   * @param occupancy the occupancy
   */
  public Occupancy save(Occupancy occupancy) {
    return occupancyRepository.save(occupancy);
  }
}
