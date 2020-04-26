package de.sakpaas.backend.service;

import de.sakpaas.backend.model.Favorite;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.v2.dto.LocationResultLocationDto;
import de.sakpaas.backend.v2.mapper.LocationMapper;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class FavoriteService {

  private final FavoriteRepository favoriteRepository;
  private final LocationMapper locationMapper;

  @Autowired
  public FavoriteService(FavoriteRepository favoriteRepository,
                         LocationMapper locationMapper) {
    this.favoriteRepository = favoriteRepository;
    this.locationMapper = locationMapper;
  }

  /**
   * Saves a Favorite.
   *
   * @param favorite the Favorite that needs to be saved.
   * @return the saved Favorite.
   */
  public Favorite save(Favorite favorite) {
    return favoriteRepository.save(favorite);
  }

  /**
   * Creates a new Favorite and saves it.
   *
   * @param userId   the User for which the Favorite should be created.
   * @param location the Location of the Favorite.
   * @return the created and saved Favorite.
   */
  public Favorite addNewFavoriteForUserAndLocation(UUID userId, Location location) {
    return save(new Favorite(userId, location));
  }

  /**
   * deletes a given Favorite.
   *
   * @param favorite the Favorite that should be deleted.
   */
  public void delete(Favorite favorite) {
    favoriteRepository.delete(favorite);
  }

  /**
   * Deletes a given Favorite by it's id.
   *
   * @param id the id of the Favorite
   */
  public void deleteById(Long id) {
    Favorite favorite = favoriteRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    delete(favorite);
  }

  /**
   * Deletes all Favorites of a Location.
   *
   * @param location the Location.
   */
  public void deleteByLocation(Location location) {
    List<Favorite> favorites = favoriteRepository.findByLocation(location);
    favorites.forEach(favoriteRepository::delete);
  }

  // TODO: this needs to be called out of keycloak when a user get's deleted

  /**
   * Deletes all Favorites of a User.
   *
   * @param userID the UUID of the User
   */
  public void deleteByUserUuid(UUID userID) {
    List<Favorite> favorites = favoriteRepository.findByUserUuid(userID);
    favorites.forEach(favoriteRepository::delete);
  }

  public List<LocationResultLocationDto> getFavoriteLocationByUserId(UUID userid) {
    List<Favorite> favorites = favoriteRepository.findByUserUuid(userid);
    return favorites.stream()
        .map(favorite -> locationMapper.mapLocationToOutputDto(favorite.getLocation()))
        .collect(Collectors.toList());
  }


  public void deleteByIdAndUUID(Long id, UUID userid) {
    Favorite favorite = favoriteRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    if (favorite.getUserUuid() == userid) {
      delete(favorite);
    } else {
      //Exception Favorit eines anderen Users nicht löschbar
    }

  }


}
