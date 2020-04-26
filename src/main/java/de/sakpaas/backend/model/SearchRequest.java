package de.sakpaas.backend.model;

import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchRequest {
  private Set<String> query;
  private CoordinateDetails coordinates;
  private List<String> brands;
  private List<Location> locations;
  private SearchResultObject nominatimResult;
  private int resultLimit;
}
