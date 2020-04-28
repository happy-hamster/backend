package de.sakpaas.backend.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SearchResultObject {

  private final CoordinateDetails coordinates;
  private final List<Location> locationList;
}
