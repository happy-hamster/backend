package de.sakpaas.backend.model;

import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OccupancyHistoryId implements Serializable {

  private long location;
  private int aggregationHour;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OccupancyHistoryId that = (OccupancyHistoryId) o;
    return aggregationHour == that.aggregationHour
        && Objects.equals(location, that.location);
  }

  @Override
  public int hashCode() {
    return Objects.hash(location, aggregationHour);
  }
}
