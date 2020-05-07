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


  @Test
  void getAllLocationTypes() {
    locationDetailsRepository.deleteAll();

    assertThat(locationDetailsRepository.getAllLocationTypes().size()).isEqualTo(0);

    locationDetailsRepository
        .save(new LocationDetails("testType1", "testOpeningHours1", "testBrand1"));

    assertThat(locationDetailsRepository.getAllLocationTypes().size()).isEqualTo(1);
    assertThat(locationDetailsRepository.getAllLocationTypes().contains("testType1"));

    locationDetailsRepository
        .save(new LocationDetails("testType2", "testOpeningHours2", "testBrand2"));

    assertThat(locationDetailsRepository.getAllLocationTypes().size()).isEqualTo(2);
    assertThat(locationDetailsRepository.getAllLocationTypes().contains("testType1"));
    assertThat(locationDetailsRepository.getAllLocationTypes().contains("testType2"));

    locationDetailsRepository
        .save(new LocationDetails("testType1", "testOpeningHours3", "testBrand3"));

    assertThat(locationDetailsRepository.getAllLocationTypes().size()).isEqualTo(2);
    assertThat(locationDetailsRepository.getAllLocationTypes().contains("testType1"));
    assertThat(locationDetailsRepository.getAllLocationTypes().contains("testType2"));

    locationDetailsRepository.deleteAll();
  }
}