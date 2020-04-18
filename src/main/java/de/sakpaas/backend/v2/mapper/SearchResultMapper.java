package de.sakpaas.backend.v2.mapper;

import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.service.OccupancyService;
import de.sakpaas.backend.service.SearchService.SearchResultObject;
import de.sakpaas.backend.v2.dto.LocationResultLocationDto;
import de.sakpaas.backend.v2.dto.LocationResultLocationDto.LocationResultCoordinatesDto;
import de.sakpaas.backend.v2.dto.SearchResultDto;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SearchResultMapper {

  private final OccupancyService occupancyService;

  @Autowired
  public SearchResultMapper(OccupancyService occupancyService) {
    this.occupancyService = occupancyService;
  }


  /**
   * Maps the given SearchResultObject to a v2 SearchResultDto.
   *
   * @param searchResultObject the SearchResultObject to be mapped
   * @return the mapped SearchResultDto
   */
  public SearchResultDto mapToOutputDto(SearchResultObject searchResultObject) {
    if (searchResultObject == null) {
      return null;
    }

    return new SearchResultDto(mapCoordinates(searchResultObject.getCoordinates()),
        mapLocations(searchResultObject.getLocationList()));
  }

  private List<LocationResultLocationDto> mapLocations(List<Location> locations) {
    return locations.stream()
        .map(location -> new LocationResultLocationDto(
            location.getId(), location.getName(),
            new LocationResultLocationDto.LocationResultLocationDetailsDto(location.getDetails()),
            new LocationResultLocationDto.LocationResultCoordinatesDto(location.getLatitude(),
                location.getLongitude()),
            new LocationResultLocationDto.LocationResultOccupancyDto(
                occupancyService.getOccupancyCalculation(location)),
            new LocationResultLocationDto.LocationResultAddressDto(location.getAddress())))
        .collect(Collectors.toList());
  }

  private LocationResultCoordinatesDto mapCoordinates(Map<String, Double> location) {
    return new LocationResultLocationDto.LocationResultCoordinatesDto(location.get("lat"),
        location.get("lon"));
  }
}
