package de.sakpaas.backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import de.sakpaas.backend.HappyHamsterTest;
import de.sakpaas.backend.model.LocationDetails;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LocationDetailsRepositoryTest extends HappyHamsterTest {

  @Autowired
  LocationDetailsRepository locationDetailsRepository;

  @Test
  void getAllBrandNames() {
    locationDetailsRepository.deleteAll();

    assertThat(locationDetailsRepository.getAllBrandNamesLower().size()).isEqualTo(0);

    locationDetailsRepository
        .save(new LocationDetails("testType", "testOpeningHours", "testBrand"));
    assertThat(locationDetailsRepository.getAllBrandNamesLower().size()).isEqualTo(1);

    locationDetailsRepository
        .save(new LocationDetails("testType", "testOpeningHours", "test Brand"));

    Set<String> result = locationDetailsRepository.getAllBrandNamesLower();
    assertThat(result.size()).isEqualTo(2);
    assertThat(result.contains("testbrand")).isTrue();
    assertThat(result.contains("test brand")).isTrue();


    locationDetailsRepository.deleteAll();
  }
}