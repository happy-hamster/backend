package de.sakpaas.backend.service;

import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.Occupancy;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OccupancyRepository extends JpaRepository<Occupancy, Long> {

  /**
   * Returns all {@link Occupancy}s for a given {@link Location} registered after the given
   * {@link ZonedDateTime}.
   *
   * @param location the {@link Location} to search for
   * @param after    the {@link ZonedDateTime} after which the {@link Occupancy}s were registered
   * @return {@link List} of {@link Occupancy}s
   */
  List<Occupancy> findByLocationAndTimestampAfter(Location location, ZonedDateTime after);

  /**
   * Returns all {@link Occupancy}s for a given {@link Location}.
   *
   * @param location the {@link Location} to search for
   * @return {@link List} of {@link Occupancy}s
   */
  List<Occupancy> findByLocation(Location location);

  // Queries for Spam-Protection

  /**
   * Returns all {@link Occupancy}s of the given user {@link UUID}, at the given {@link Location}, registered
   * after the given {@link ZonedDateTime}.
   *
   * @param location the {@link Location} to search for
   * @param userUuid the user ({@link UUID}) to search for
   * @param after    the {@link ZonedDateTime} after which the {@link Occupancy}s were registered
   * @return {@link List} of {@link Occupancy}s
   */
  List<Occupancy> findByLocationAndUserUuidAndTimestampAfter(Location location, UUID userUuid,
                                                             ZonedDateTime after);

  /**
   * Returns all {@link Occupancy}s where the request hash matches the given request hash,
   * at the given {@link Location}, registered after the given {@link ZonedDateTime}.
   *
   * @param location    the {@link Location} to search for
   * @param requestHash the hash of the request to search for
   * @param after       the {@link ZonedDateTime} after which the {@link Occupancy}s were registered
   * @return {@link List} of {@link Occupancy}s
   */
  List<Occupancy> findByLocationAndRequestHashAndTimestampAfter(Location location,
                                                                byte[] requestHash,
                                                                ZonedDateTime after);
  /**
   * Returns all {@link Occupancy}s of the given user {@link UUID}, registered after
   * the given {@link ZonedDateTime}.
   *
   * @param userUuid the user ({@link UUID}) to search for
   * @param after       the {@link ZonedDateTime} after which the {@link Occupancy}s were registered
   * @return {@link List} of {@link Occupancy}s
   */

  List<Occupancy> findByUserUuidAndTimestampAfter(UUID userUuid, ZonedDateTime after);

  /**
   * Returns all {@link Occupancy}s where the request hash matches the given request hash,
   * registered after the given {@link ZonedDateTime}.
   *
   * @param requestHash the hash of the request to search for
   * @param after       the {@link ZonedDateTime} after which the {@link Occupancy}s were registered
   * @return {@link List} of {@link Occupancy}s
   */
  List<Occupancy> findByRequestHashAndTimestampAfter(byte[] requestHash, ZonedDateTime after);
}
