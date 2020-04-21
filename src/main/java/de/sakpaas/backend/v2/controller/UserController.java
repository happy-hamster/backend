package de.sakpaas.backend.v2.controller;

import static org.springframework.http.HttpStatus.OK;

import de.sakpaas.backend.dto.UserInfoDto;
import de.sakpaas.backend.model.Favorite;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.service.FavoriteRepository;
import de.sakpaas.backend.service.UserService;
import de.sakpaas.backend.v2.dto.LocationResultLocationDto;
import de.sakpaas.backend.v2.mapper.LocationMapper;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v2/user")
public class UserController {

  final UserService userService;
  final FavoriteRepository favoriteRepository;
  final LocationMapper locationMapper;

  /**
   * Constructor for Services.
   */
  public UserController(UserService userService, FavoriteRepository favoriteRepository,
                        LocationMapper locationMapper) {
    this.userService = userService;
    this.favoriteRepository = favoriteRepository;
    this.locationMapper = locationMapper;
  }

  /**
   * Get Endpoint that returns information of the user specified in the token.
   *
   * @param header The Authorization-Header that has to be provided in the request
   * @return Returns the UserInfoDto
   */
  @GetMapping("/info")
  public ResponseEntity<UserInfoDto> getUserInfo(@RequestHeader("Authorization") String header,
                                                 Principal principal) {
    return new ResponseEntity<>(userService.getUserInfo(header, principal), OK);
  }

  /**
   * Get Endpoint that returns the favorites of the user specified in the token.
   *
   * @param header The Authorization-Header that has to be provided in the request.
   * @return Returns an Array of Locations.
   */
  @GetMapping("/self/favorites")
  public ResponseEntity<List<LocationResultLocationDto>> getFavorites(
      @RequestHeader("Authorization") String header, Principal principal) {
    UserInfoDto userInfo = userService.getUserInfo(header, principal);
    List<Favorite> favorites = favoriteRepository.findByUserUuid(UUID.fromString(userInfo.getId()));
    List<LocationResultLocationDto> response = favorites.stream()
        .map(favorite -> {
          Location location = favorite.getLocation();
          return locationMapper.mapToOutputDto(location);
        })
        .collect(Collectors.toList());

    return new ResponseEntity<>(response, OK);
  }
}
