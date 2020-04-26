package de.sakpaas.backend.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchRequest {
  private List<String> query;
  private CoordinateDetails coordinates;
  private List<String> brands;
  private List<Location> locations;
  private SearchResultObject nominatimResult;
  private int resultLimit;
}
