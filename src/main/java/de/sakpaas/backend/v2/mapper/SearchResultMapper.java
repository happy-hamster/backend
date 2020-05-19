package de.sakpaas.backend.v2.mapper;

import de.sakpaas.backend.dto.UserInfoDto;
import de.sakpaas.backend.model.CoordinateDetails;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.SearchResultObject;
import de.sakpaas.backend.v2.dto.LocationResultLocationDto;
import de.sakpaas.backend.v2.dto.LocationResultLocationDto.LocationResultCoordinatesDto;
import de.sakpaas.backend.v2.dto.SearchResultDto;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SearchResultMapper {

  private final LocationMapper locationMapper;

  /**
   * Maps the search result object to a SearchResultDto.
   *
   * @param locationMapper The Mapper for Locations
   */
  @Autowired
  public SearchResultMapper(
      LocationMapper locationMapper) {
    this.locationMapper = locationMapper;
  }

  /**
   * Maps the given SearchResultObject to a v2 SearchResultDto.
   *
   * @param searchResultObject the SearchResultObject to be mapped
   * @return the mapped SearchResultDto
   */
  public SearchResultDto mapSearchResultToOutputDto(SearchResultObject searchResultObject) {
    if (searchResultObject == null) {
      return null;
    }

    return new SearchResultDto(mapCoordinates(searchResultObject.getCoordinates()),
        mapLocations(new ArrayList<>(searchResultObject.getLocationList())));
  }

  /**
   * Maps the given SearchResultObject to a v2 SearchResultDto.
   *
   * @param searchResultObject the SearchResultObject to be mapped
   * @param user               the User for which the flag should be set for
   * @return the mapped SearchResultDto
   */
  public SearchResultDto mapSearchResultToOutputDto(SearchResultObject searchResultObject,
                                                    UserInfoDto user) {
    if (searchResultObject == null) {
      return null;
    }

    return new SearchResultDto(mapCoordinates(searchResultObject.getCoordinates()),
        mapLocations(new ArrayList<>(searchResultObject.getLocationList()), user));
  }

  /**
   * Helper method to map the locations.
   *
   * @param locations The list of locations to be mapped
   * @return The mapped LocationResultLocationDto
   */
  private List<LocationResultLocationDto> mapLocations(List<Location> locations) {
    List<LocationResultLocationDto> resultLocationDtoList = new ArrayList<>();
    for (Location location : locations) {
      resultLocationDtoList.add(locationMapper.mapLocationToOutputDto(location));
    }
    return resultLocationDtoList;
  }

  /**
   * Helper method to map the locations.
   *
   * @param locations The list of locations to be mapped
   * @param user      the User for which the flag should be set for
   * @return The mapped LocationResultLocationDto
   */
  private List<LocationResultLocationDto> mapLocations(List<Location> locations, UserInfoDto user) {
    List<LocationResultLocationDto> resultLocationDtoList = new ArrayList<>();
    for (Location location : locations) {
      resultLocationDtoList.add(locationMapper.mapLocationToOutputDto(location, user));
    }
    return resultLocationDtoList;
  }

  /**
   * Helper method the map the coordinates.
   *
   * @param coordinates The coordinates to be mapped
   * @return The mapped LocationResultCoordinatesDto
   */
  private LocationResultCoordinatesDto mapCoordinates(CoordinateDetails coordinates) {
    return new LocationResultLocationDto.LocationResultCoordinatesDto(coordinates.getLatitude(),
        coordinates.getLongitude());
  }
}
