package de.sakpaas.backend.service;

import de.sakpaas.backend.model.Favorite;
import de.sakpaas.backend.model.Location;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
  List<Favorite> findByUserUuid(UUID userUuid);

  List<Favorite> findByLocation(Location location);
}
