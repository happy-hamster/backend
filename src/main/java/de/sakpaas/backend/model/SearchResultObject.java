package de.sakpaas.backend.model;

import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SearchResultObject {

  private final CoordinateDetails coordinates;
  private final Set<Location> locationList;
}
