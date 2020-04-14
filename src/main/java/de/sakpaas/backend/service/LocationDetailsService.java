package de.sakpaas.backend.service;

import de.sakpaas.backend.model.LocationDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LocationDetailsService {

  private final LocationDetailsRepository locationDetailsRepository;

  @Autowired
  public LocationDetailsService(LocationDetailsRepository locationDetailsRepository) {
    this.locationDetailsRepository = locationDetailsRepository;
  }

  public LocationDetails save(LocationDetails address) {
    return locationDetailsRepository.save(address);
  }

}
