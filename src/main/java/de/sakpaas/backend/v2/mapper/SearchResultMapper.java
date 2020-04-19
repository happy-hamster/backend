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

  /**
   * Maps the search result object to a SearchResultDto.
   *
   * @param occupancyService The occupancy service
   */
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

  /**
   * Helper method to map the locations.
   *
   * @param locations The list of locations to be mapped
   * @return The mapped LocationResultLocationDto
   */
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

  /**
   * Helper method the map the coordinates.
   *
   * @param coordinates The coordinates to be mapped
   * @return The mapped LocationResultCoordinatesDto
   */
  private LocationResultCoordinatesDto mapCoordinates(Map<String, Double> coordinates) {
    return new LocationResultLocationDto.LocationResultCoordinatesDto(coordinates.get("lat"),
        coordinates.get("lon"));
  }
}
