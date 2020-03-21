package de.sakpaas.backend.service;

import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.Presence;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Date;

@Service
public class PresenceService {

  private final PresenceRepository presenceRepository;

  public PresenceService(PresenceRepository presenceRepository) {
    this.presenceRepository = presenceRepository;
  }

  /***
   * Creats a new Presence instance based on a Checkin event
   * @param locationId Id of the Location the presence is linked to
   */
  public void addNewCheckin(String locationId) {
    addNewCheckin(locationId);
  }

  /***
   * Creats a new Presence instance based on a Checkin event
   * @param locationId Id of the Location the presence is linked to
   * @param duration Duration of the presence in MS
   */
  public void addNewCheckin(String locationId, Long duration) {
    Date now = new Date();
    presenceRepository.save(new Presence(locationId, now, new Date(now.getTime() + duration)));
  }

  public void getActiveCheckins(Location location) {
    presenceRepository.findByLocationAndCheckOutBeforeAndCheckInAfter(location, ZonedDateTime.now());
  }
}
