package de.sakpaas.backend.model;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "OCCUPANCY_HISTORY")
@IdClass(OccupancyHistoryId.class)
public class OccupancyHistory {

  @Id
  @ManyToOne(optional = false)
  @JoinColumn(name = "LOCATION_ID", referencedColumnName = "ID", nullable = false)
  private Location location;

  @Id
  @Column(name = "AGGREGATION_HOUR", nullable = false)
  @Range(min = 0, max = 168)
  private int aggregationHour;

  @Column(name = "OCCUPANCY_SUM")
  private double occupancySum;

  @Column(name = "OCCUPANCY_COUNT")
  private int occupancyCount;

  /**
   * Creates a new {@link OccupancyHistory} with {@code sum = 0.0} and {@code count = 1}.
   *
   * @param location        the {@link Location}
   * @param aggregationHour the aggregation hour
   */
  public OccupancyHistory(Location location, int aggregationHour) {
    this.location = location;
    this.aggregationHour = aggregationHour;
    this.occupancySum = 0.0;
    this.occupancyCount = 0;
  }

  /**
   * Creates a new {@link OccupancyHistory}.
   *
   * @param location        the {@link Location}
   * @param aggregationHour the aggregation hour
   * @param occupancySum    the {@link Occupancy} sum
   * @param occupancyCount  tje {@link Occupancy} count
   */
  public OccupancyHistory(Location location, int aggregationHour, double occupancySum,
                          int occupancyCount) {
    this.location = location;
    this.aggregationHour = aggregationHour;
    this.occupancySum = occupancySum;
    this.occupancyCount = occupancyCount;
  }

  public void increment(double occupancy) {
    this.occupancySum += occupancy;
    this.occupancyCount++;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OccupancyHistory that = (OccupancyHistory) o;
    return aggregationHour == that.aggregationHour
        && Double.compare(that.occupancySum, occupancySum) == 0
        && occupancyCount == that.occupancyCount
        && Objects.equals(
        (location == null) ? null : location.getId(),
        (that.location == null) ? null : that.location.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash((location == null) ? null : location.getId(),
        aggregationHour, occupancySum, occupancyCount);
  }
}
