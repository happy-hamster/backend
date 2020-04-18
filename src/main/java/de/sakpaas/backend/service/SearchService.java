package de.sakpaas.backend.service;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

  private final SearchMappingService searchMappingService;

  @Autowired
  public SearchService(SearchMappingService searchMappingService) {
    this.searchMappingService = searchMappingService;
  }

  public void search(String query) {
    Map<String, Double> nominatimResultLocationDtoList =
        searchMappingService.search(query);
  }


}
