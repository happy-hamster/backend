package de.sakpaas.backend.service;

import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.Presence;
import java.time.ZonedDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PresenceService {

  private final PresenceRepository presenceRepository;

  @Value("${app.presence.duration}")
  private int confDuration;

  @Autowired
  public PresenceService(PresenceRepository presenceRepository) {
    this.presenceRepository = presenceRepository;
  }

  /**
   * Creates a new Presence instance based on a Check-In event.
   *
   * @param location Duration of the presence in Minutes
   */
  public void addNewCheckin(Location location) {
    addNewCheckin(location, confDuration);
  }

  /**
   * Creates a new Presence instance based on a Check-In event.
   *
   * @param location The Location the presence is linked to
   * @param duration Duration of the presence in Minutes
   */
  public void addNewCheckin(Location location, int duration) {
    presenceRepository.save(
        new Presence(location, ZonedDateTime.now(), ZonedDateTime.now().plusMinutes(duration)));
  }
}
