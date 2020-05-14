package de.sakpaas.backend.service;

import static java.time.ZonedDateTime.now;

import com.google.common.annotations.VisibleForTesting;
import de.sakpaas.backend.model.AccumulatedOccupancy;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.Occupancy;
import de.sakpaas.backend.util.OccupancyAccumulationConfiguration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OccupancyService {

  private final OccupancyRepository occupancyRepository;
  private final OccupancyAccumulationConfiguration config;

  @Autowired
  public OccupancyService(OccupancyRepository occupancyRepository,
                          OccupancyAccumulationConfiguration occupancyAccumulationConfiguration) {
    this.occupancyRepository = occupancyRepository;
    this.config = occupancyAccumulationConfiguration;
  }

  /**
   * Calculates the average occupancy based on the given reports.
   *
   * @param occupancies the occupancies to calculate with
   * @param time        the time to calculate with
   * @return the average occupancy
   */
  private Double calculateAccumulatedOccupancy(List<Occupancy> occupancies, ZonedDateTime time) {
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
      double factor = calculateAccumulationFactor(minutes);

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
  double calculateAccumulationFactor(double x) {
    // See documentation for a more understandable formula
    double base = 1.0 + (1.0 / config.getFactorA());
    double exponent = -Math.pow(-x - config.getConstant(), 2) / config.getFactorB();
    return (x < -config.getConstant())
        ? (1.0 - config.getMinimum()) * Math.pow(base, exponent) + config.getMinimum()
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
        now().minusMinutes(config.getDuration()));

    return new AccumulatedOccupancy(
        calculateAccumulatedOccupancy(occupancies, time),
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



  /**
   * Returns the hour of the week, based on the Date. Public Holidays are treated like a sunday.
   *
   * @param date The Date
   * @return The hour of the week
   */
  private int getAggregationHour(Date date) {
    return 8;
  }


  /**
   * Returns the Occupancy based on the historical data.
   *
   * @param location The Location you want an occupancy for
   * @return The Occupancy
   */
  private double getOccupancyFromHistory(Location location, List<Integer> aggregationHours) {
    return 1.0;
  }


  /**
   * Calculating Occupancy based on Statista statistics and the Date.
   *
   * @return The Occupancy
   */
  private double getOccupancyFromStatista(Location location, List<Integer> aggregationHours) {
    return 1.0;
  }
}


