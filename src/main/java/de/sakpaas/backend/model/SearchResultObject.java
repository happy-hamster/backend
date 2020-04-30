package de.sakpaas.backend.model;

import java.util.List;
import lombok.Data;

@Data
public class SearchResultObject {

  private final CoordinateDetails coordinates;
  private final List<Location> locationList;
}
