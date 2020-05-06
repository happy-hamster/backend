package de.sakpaas.backend.v2.controller;

import static org.springframework.http.HttpStatus.OK;

import de.sakpaas.backend.dto.UserInfoDto;
import de.sakpaas.backend.exception.InvalidLocationException;
import de.sakpaas.backend.model.Favorite;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.service.FavoriteService;
import de.sakpaas.backend.service.LocationService;
import de.sakpaas.backend.service.UserService;
import de.sakpaas.backend.v2.dto.LocationResultLocationDto;
import de.sakpaas.backend.v2.mapper.LocationMapper;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v2/users")
public class UserController {

  final UserService userService;
  final LocationMapper locationMapper;
  final FavoriteService favoriteService;
  final LocationService locationService;

  /**
   * Constructor for Services.
   */
  public UserController(UserService userService,
                        LocationMapper locationMapper,
                        FavoriteService favoriteService,
                        LocationService locationService) {
    this.userService = userService;
    this.locationMapper = locationMapper;
    this.favoriteService = favoriteService;
    this.locationService = locationService;
  }

  /**
   * Get Endpoint that returns information of the user specified in the token.
   *
   * @param header The Authorization-Header that has to be provided in the request
   * @return Returns the UserInfoDto
   */
  @GetMapping("self/info")
  public ResponseEntity<UserInfoDto> getUserInfo(@RequestHeader("Authorization") String header) {
    return new ResponseEntity<>(userService.getUserInfo(header), OK);
  }

  /**
   * Get Endpoint that returns the favorites of the user specified in the token.
   *
   * @param header The Authorization-Header that has to be provided in the request.
   * @return Returns an Array of Locations.
   */
  @GetMapping("/self/favorites")
  public ResponseEntity<List<LocationResultLocationDto>> getFavorites(
      @RequestHeader("Authorization") String header) {

    UserInfoDto userInfo = userService.getUserInfo(header);

    List<Favorite> favorites = favoriteService.findByUserUuid(userInfo.getId());
    List<LocationResultLocationDto> response = favorites.stream()
        .map(favorite -> locationMapper.mapLocationToOutputDto(favorite.getLocation(), userInfo))
        .collect(Collectors.toList());

    return new ResponseEntity<>(response, OK);
  }

  /**
   * Post Endpoint that creates a favorite.
   *
   * @param locationId the location Id of the new favorite
   * @param header     The Authorization-Header that has to be provided in the request.
   * @return Returns a ResponseEntity
   */
  @PostMapping("/self/favorites/{id}")
  public ResponseEntity<LocationResultLocationDto> postFavorite(
      @PathVariable("id") Long locationId,
      @RequestHeader("Authorization") String header) {

    UserInfoDto userInfo = userService.getUserInfo(header);
    Location location = locationService.getById(locationId)
        .orElseThrow(() -> new InvalidLocationException(locationId));

    Favorite favorite = new Favorite(userInfo.getId(), location);
    favoriteService.saveUnique(favorite);

    return new ResponseEntity<>(locationMapper.mapLocationToOutputDto(location, userInfo), OK);
  }

  /**
   * Delete Endpoint that deletes a favorite.
   *
   * @param locationId the location Id of the new favorite
   * @param header     The Authorization-Header that has to be provided in the request.
   * @return Returns a ResponseEntity
   */
  @DeleteMapping("/self/favorites/{id}")
  public ResponseEntity<LocationResultLocationDto> deleteFavorite(
      @PathVariable("id") Long locationId,
      @RequestHeader("Authorization") String header) {

    UserInfoDto userInfo = userService.getUserInfo(header);
    Location location = locationService.getById(locationId)
        .orElseThrow(() -> new InvalidLocationException(locationId));

    favoriteService.delete(location, userInfo.getId());

    return new ResponseEntity<>(locationMapper.mapLocationToOutputDto(location, userInfo), OK);
  }
}
