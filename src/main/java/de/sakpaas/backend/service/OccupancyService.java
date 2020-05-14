package de.sakpaas.backend.service;

import static java.time.ZonedDateTime.now;

import com.google.common.annotations.VisibleForTesting;
import de.sakpaas.backend.exception.TooManyRequestsException;
import de.sakpaas.backend.model.AccumulatedOccupancy;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.Occupancy;
import de.sakpaas.backend.util.OccupancyAccumulationConfiguration;
import de.sakpaas.backend.util.OccupancyReportLimitsConfiguration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OccupancyService {

  private final OccupancyRepository occupancyRepository;
  private final OccupancyAccumulationConfiguration config;
  private final OccupancyReportLimitsConfiguration configReportLimits;


  /**
   * Default Constructor. Handles the Dependency Injection.
   *
   * @param occupancyRepository the {@link OccupancyRepository}
   * @param occupancyAccumulationConfiguration the {@link OccupancyAccumulationConfiguration}
   * @param configReportLimits the {@link OccupancyReportLimitsConfiguration}
   */
  @Autowired
  public OccupancyService(OccupancyRepository occupancyRepository,
                          OccupancyAccumulationConfiguration occupancyAccumulationConfiguration,
                          OccupancyReportLimitsConfiguration configReportLimits) {
    this.occupancyRepository = occupancyRepository;
    this.config = occupancyAccumulationConfiguration;
    this.configReportLimits = configReportLimits;
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
   * Checks if there are too many occupancies given in the last * minutes.
   *
   * @param location the occupancies for the location
   * @param uuid     uuid of the user
   * @throws TooManyRequestsException if too many occupancies were reported
   */
  public void checkReportLimit(Location location, UUID uuid) throws TooManyRequestsException {
    if (!configReportLimits.isEnabled()) {
      return;
    }

    ZonedDateTime zoneDateTime = ZonedDateTime.now();
    List<Occupancy> occupanciesLocations = occupancyRepository
        .findByLocationAndUserUuidAndTimestampAfter(location, uuid,
            zoneDateTime.minusMinutes(configReportLimits.getLocationPeriod()));
    if (occupanciesLocations.size() >= configReportLimits.getLocationLimit()) {
      throw new TooManyRequestsException();
    }
    List<Occupancy> occupancies =
        occupancyRepository.findByUserUuidAndTimestampAfter(uuid,
            zoneDateTime.minusMinutes(configReportLimits.getGlobalPeriod()));
    if (occupancies.size() >= configReportLimits.getGlobalLimit()) {
      throw new TooManyRequestsException();
    }
  }

  /**
   * Checks if there are too many occupancies given in the last * minutes.
   *
   * @param location    the occupancies for the location
   * @param requestHash request hash of the user
   * @throws TooManyRequestsException if too many occupancies were reported
   */
  public void checkReportLimit(Location location, byte[] requestHash)
      throws TooManyRequestsException {
    if (!configReportLimits.isEnabled()) {
      return;
    }
    
    ZonedDateTime zoneDateTime = ZonedDateTime.now();
    List<Occupancy> occupanciesLocations = occupancyRepository
        .findByLocationAndRequestHashAndTimestampAfter(location, requestHash,
            zoneDateTime.minusMinutes(configReportLimits.getLocationPeriod()));
    if (occupanciesLocations.size() >= configReportLimits.getLocationLimit()) {
      throw new TooManyRequestsException();
    }
    List<Occupancy> occupancies = occupancyRepository
        .findByRequestHashAndTimestampAfter(requestHash,
            zoneDateTime.minusMinutes(configReportLimits.getGlobalPeriod()));
    if (occupancies.size() >= configReportLimits.getGlobalLimit()) {
      throw new TooManyRequestsException();
    }
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
