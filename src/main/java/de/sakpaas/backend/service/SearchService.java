package de.sakpaas.backend.service;

import de.sakpaas.backend.model.Location;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

  private final LocationService locationService;
  private final SearchMappingService searchMappingService;

  @Autowired
  public SearchService(LocationService locationService,
      SearchMappingService searchMappingService) {
    this.locationService = locationService;
    this.searchMappingService = searchMappingService;
  }

  public SearchResultObject search(String query) {
    Map<String, Double> nominatimResultLocationDtoList =
        searchMappingService.search(query);
    List<Location> locationList =
        locationService.findByCoordinates(nominatimResultLocationDtoList.get("lat"),
            nominatimResultLocationDtoList.get("lon"));

    return new SearchResultObject(nominatimResultLocationDtoList, locationList);
  }

  @AllArgsConstructor
  @Getter
  public static class SearchResultObject {

    private final Map<String, Double> coordinates;
    private final List<Location> locationList;
  }
}
