package de.sakpaas.backend.model;

import java.util.Set;
import lombok.Data;

@Data
public class SearchResultObject {

  private final CoordinateDetails coordinates;
  private final Set<Location> locationList;
}
