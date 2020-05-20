package de.sakpaas.backend.service;

import static java.time.ZonedDateTime.now;

import com.google.common.annotations.VisibleForTesting;
import de.jollyday.HolidayManager;
import de.jollyday.ManagerParameters;
import de.sakpaas.backend.exception.TooManyRequestsException;
import de.sakpaas.backend.model.AccumulatedOccupancy;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.Occupancy;
import de.sakpaas.backend.model.OccupancyHistory;
import de.sakpaas.backend.util.OccupancyAccumulationConfiguration;
import de.sakpaas.backend.util.OccupancyReportLimitsConfiguration;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OccupancyService {

  private final OccupancyRepository occupancyRepository;
  private final OccupancyAccumulationConfiguration config;
  private final OccupancyReportLimitsConfiguration configReportLimits;
  private final OccupancyHistoryRepository occupancyHistoryRepository;

  /**
   * Default Constructor. Handles the Dependency Injection.
   *
   * @param occupancyRepository                the {@link OccupancyRepository}
   * @param occupancyAccumulationConfiguration the {@link OccupancyAccumulationConfiguration}
   * @param configReportLimits                 the {@link OccupancyReportLimitsConfiguration}
   * @param occupancyHistoryRepository         The OccupancyHistoryRepository
   */
  @Autowired
  public OccupancyService(OccupancyRepository occupancyRepository,
                          OccupancyAccumulationConfiguration occupancyAccumulationConfiguration,
                          OccupancyReportLimitsConfiguration configReportLimits,
                          OccupancyHistoryRepository occupancyHistoryRepository) {
    this.occupancyRepository = occupancyRepository;
    this.config = occupancyAccumulationConfiguration;
    this.configReportLimits = configReportLimits;
    this.occupancyHistoryRepository = occupancyHistoryRepository;
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
    // TODO Muss neu implementiert werden. Task bereits angelegt.

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

    ZonedDateTime zoneDateTime = now();
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

    ZonedDateTime zoneDateTime = now();
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
   * Returns True if the Date is a Public Holiday.
   *
   * @param date The Date
   * @return Result if it is a public holiday
   */
  @VisibleForTesting
  boolean isPublicHoliday(ZonedDateTime date, Location location) {
    LocalDate testDate = date.toLocalDate();
    String country = location.getAddress().getCountry();
    HolidayManager holidayManager = HolidayManager
        .getInstance(ManagerParameters.create(country, null));

    return holidayManager.isHoliday(testDate, country.toLowerCase());
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
   * Gets the Occupancy from recently submitted reports. Is Null if there are no in the range.
   *
   * @param location The Location
   * @return The Occupancy
   */
  private AccumulatedOccupancy getLiveOccupancy(Location location) {
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
   * Returns the hour of the week, based on the Date. Public Holidays are treated like a sunday.
   * The number of the day corresponds to {@link DayOfWeek}{@code .ordinal()}, thus monday is equal
   * to 0 and sunday is equal to 6.
   *
   * @param date The Date
   * @return The hour of the week
   */
  @VisibleForTesting
  int getAggregationHour(ZonedDateTime date, Location location) {
    DayOfWeek day = date.getDayOfWeek();
    // public holidays should be handled like sundays
    if (isPublicHoliday(date, location)) {
      day = DayOfWeek.SUNDAY;
    }
    // convert enum to int
    int dayOrdinal = day.ordinal();
    // get hour of the day
    int hour = date.getHour();

    // compute aggregation hour
    // all hours of one day have to not overlap with other days
    return dayOrdinal * (int) ChronoUnit.DAYS.getDuration().toHours() + hour;
  }


  /**
   * Returns the Occupancy based on the historical data.
   *
   * @param location The Location you want an occupancy for
   * @return The Occupancy
   */
  protected AccumulatedOccupancy getOccupancyFromHistory(Location location,
                                                         List<Integer> aggregationHours) {
    Set<OccupancyHistory> occupancyHistories = new HashSet<>();
    for (Integer aggregationHour : aggregationHours) {
      occupancyHistories
          .addAll(occupancyHistoryRepository
              .findByLocationAndAggregationHour(location, aggregationHour));
    }
    OptionalDouble avg = occupancyHistories.stream()
        .mapToDouble(OccupancyHistory::getOccupancy)
        .average();
    if (avg.isPresent()) {
      int sum = occupancyHistories.stream()
          .mapToInt(OccupancyHistory::getOccupancyCount)
          .sum();
      return new AccumulatedOccupancy(avg.getAsDouble(), sum, ZonedDateTime.now());
    } else {
      return new AccumulatedOccupancy(null, 0, ZonedDateTime.now());
    }
  }


  /**
   * Calculating Occupancy based on our own data or statista statistics and the Date.
   *
   * @return The Occupancy
   */
  private AccumulatedOccupancy getOccupancyFromStatistic(List<Integer> aggregationHours) {
    return new AccumulatedOccupancy(1.0, 1, ZonedDateTime.now());
  }
}


