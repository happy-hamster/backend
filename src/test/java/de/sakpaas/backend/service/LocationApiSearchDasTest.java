package de.sakpaas.backend.service;


import static org.junit.jupiter.api.Assertions.assertEquals;

import de.sakpaas.backend.HappyHamsterTest;
import de.sakpaas.backend.dto.OsmResultLocationListDto;
import de.sakpaas.backend.util.ImportConfiguration;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
class LocationApiSearchDasTest extends HappyHamsterTest {

  final String country = "DE";
  @Autowired
  LocationApiSearchDas locationApiSearchDas;

  @Test
  void testEmptyShoptypeList() {
    ImportConfiguration importConfiguration = new ImportConfiguration();


    importConfiguration.setShoptypes(new ArrayList<>());
    List<OsmResultLocationListDto.OsmResultLocationDto> results =
        locationApiSearchDas.getLocationsForCountry(importConfiguration);

    assertEquals(results, new ArrayList<>(), "Result List should be empty");

  }


}