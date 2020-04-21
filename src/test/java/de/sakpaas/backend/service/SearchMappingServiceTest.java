package de.sakpaas.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.sakpaas.backend.HappyHamsterTest;
import de.sakpaas.backend.dto.NominatimSearchResultListDto.NominatimResultLocationDto;
import de.sakpaas.backend.model.CoordinateDetails;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SearchMappingServiceTest extends HappyHamsterTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(SearchMappingServiceTest.class);
  @Autowired
  SearchMappingService searchMappingService;

  @Test
  public void testCalcCenter() {
    final NominatimResultLocationDto internalList1 = new NominatimResultLocationDto(3, 5);
    final NominatimResultLocationDto internalList2 = new NominatimResultLocationDto(5, 3);
    final CoordinateDetails coordinateDetails = new CoordinateDetails(4.0, 4.0);

    assertEquals(coordinateDetails, searchMappingService
        .calculateCenter(new ArrayList<>(Arrays.asList(internalList1, internalList2))));
  }

  @Test
  public void testCalcCenter2() {
    final NominatimResultLocationDto internalList1 =
        new NominatimResultLocationDto(3.123, 5.123);
    final CoordinateDetails coordinateDetails = new CoordinateDetails(3.123, 5.123);

    assertEquals(coordinateDetails, searchMappingService
        .calculateCenter(new ArrayList<>(Collections.singletonList(internalList1))));
  }

  @Test
  public void testEmptyMap() {
    final String url = "https://nominatim.openstreetmap.org/search/Limburgerhof%20Rewe?format=json";
    assertEquals(new ArrayList<>(), searchMappingService.makeRequest(url));
  }

  @Test
  public void testResultMap() {
    final String url = "https://nominatim.openstreetmap.org/search/Edeka%20Hochdorf?format=json";
    final List<NominatimResultLocationDto> resultList = searchMappingService.makeRequest(url);
    LOGGER.info(resultList.toString());
    final CoordinateDetails center = searchMappingService.calculateCenter(resultList);
    LOGGER.info(center.toString());

    assertTrue(center.getLatitude() > 0);
    assertTrue(center.getLongitude() > 0);
  }

  @Test
  public void testNonEmptyResultList() {
    String url = "https://nominatim.openstreetmap.org/search/Mannheim?format=json";
    assertNotEquals("[]", searchMappingService.makeRequest(url).toString());
    LOGGER.info(searchMappingService.makeRequest(url).toString());
    url = "https://nominatim.openstreetmap.org/search/Rewe?format=json";
    assertNotEquals("[]", searchMappingService.makeRequest(url).toString());
    url = "https://nominatim.openstreetmap.org/search/Limburgerhof?format=json";
    assertNotEquals("[]", searchMappingService.makeRequest(url).toString());
  }
}
