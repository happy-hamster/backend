package de.sakpaas.backend.model;

import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchRequest {
  private Set<String> query;
  private CoordinateDetails coordinates;
  private Set<String> brands;
  private Set<Location> locations;
  private SearchResultObject nominatimResult;
  private int resultLimit;
}
