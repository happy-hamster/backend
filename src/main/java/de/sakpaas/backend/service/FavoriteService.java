package de.sakpaas.backend.service;

import de.sakpaas.backend.model.Favorite;
import de.sakpaas.backend.model.Location;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;


@Service
public class FavoriteService {

  private final FavoriteRepository favoriteRepository;


  @Autowired
  public FavoriteService(FavoriteRepository favoriteRepository) {
    this.favoriteRepository = favoriteRepository;
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
   * Saves the given Favorite but only if there is not already a Favorite for this user and
   * Location.
   *
   * @param favorite the Favorite to save
   * @return the saved Favorite
   */
  public Favorite saveUnique(Favorite favorite) {
    return favoriteRepository.exists(Example.of(favorite))
        ? null
        : save(favorite);
  }

  /**
   * Returns all Favorites of the given User.
   *
   * @param userId the UUID of the User
   * @return the Favorites of the User
   */
  public List<Favorite> findByUserUuid(UUID userId) {
    return favoriteRepository.findByUserUuid(userId);
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
   * Deletes the favorite of an user at a given location.
   *
   * @param userId   the UUID of the User
   * @param location the location of the favorite
   */

  public void delete(Location location, UUID userId) {
    favoriteRepository.deleteAll(favoriteRepository.findByUserUuidAndLocation(userId, location));
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
}
