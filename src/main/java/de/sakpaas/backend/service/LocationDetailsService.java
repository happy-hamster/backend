package de.sakpaas.backend.service;

import de.sakpaas.backend.model.LocationDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LocationDetailsService {

    private LocationDetailsRepository locationDetailsRepository;

    @Autowired
    public LocationDetailsService(LocationDetailsRepository locationDetailsRepository) {
        this.locationDetailsRepository = locationDetailsRepository;
    }

    public Optional<LocationDetails> getById(long id) {
        return locationDetailsRepository.findById(id);
    }

    public LocationDetails save(LocationDetails address) {
        return locationDetailsRepository.save(address);
    }

}
