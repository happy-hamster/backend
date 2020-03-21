package de.sakpaas.backend.service;

import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.Presence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class PresenceService {

  private final PresenceRepository presenceRepository;

  @Value("${app.presence.duration}")
  private int confDuration;

  public PresenceService(PresenceRepository presenceRepository) {
    this.presenceRepository = presenceRepository;
  }

  /***
   * Creats a new Presence instance based on a Checkin event
   * @param location Duration of the presence in Minutes
   */
  public void addNewCheckin(Location location) {
    addNewCheckin(location, confDuration);
  }

  /***
   * Creats a new Presence instance based on a Checkin event
   * @param location The Location the presence is linked to
   * @param duration Duration of the presence in Minutes
   */
  public void addNewCheckin(Location location, int duration) {
    presenceRepository.save(new Presence(location, ZonedDateTime.now(), ZonedDateTime.now().plusMinutes(duration)));
  }

  public void getActiveCheckins(Location location) {
    presenceRepository.findByLocationAndCheckOutBeforeAndCheckInAfter(location, ZonedDateTime.now());
  }
}
