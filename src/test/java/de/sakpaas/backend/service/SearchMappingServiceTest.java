package de.sakpaas.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import de.sakpaas.backend.HappyHamsterTest;
import de.sakpaas.backend.dto.NominatimSearchResultListDto.NominatimResultLocationDto;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

  private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
  @Autowired
  SearchMappingService searchMappingService;

  @Test
  public void testCalcAvg() {
    final List<Double> doubleList = new ArrayList<>(Arrays.asList(3.0, 5.0));
    assertEquals(4, searchMappingService.calculateAvg(doubleList));
  }

  @Test
  public void testCalcAvg2() {
    final List<Double> doubleList = new ArrayList<>(Arrays.asList(1.0, 11.0));
    assertEquals(6, searchMappingService.calculateAvg(doubleList));
  }

  @Test
  public void testCalcCenter() {
//    final NominatimResultLocationDto internalList1 = new NominatimResultLocationDto(1, "3", "5");
//    final NominatimResultLocationDto internalList2 = new NominatimResultLocationDto(1, "3", "5");
    final NominatimResultLocationDto internalList1 = new NominatimResultLocationDto(3, 5);
    final NominatimResultLocationDto internalList2 = new NominatimResultLocationDto(5, 3);

    final Map<String, Double> expectedMap = new HashMap<>();
    expectedMap.put("lat", 4.0);
    expectedMap.put("lon", 4.0);
    assertEquals(expectedMap, searchMappingService
        .calculateCenter(new ArrayList<>(Arrays.asList(internalList1, internalList2))));
  }

  @Test
  public void testCalcCenter2() {
    final NominatimResultLocationDto internalList1 =
        new NominatimResultLocationDto(3.123, 5.123);

    final Map<String, Double> expectedMap = new HashMap<>();
    expectedMap.put("lat", 3.123);
    expectedMap.put("lon", 5.123);
    assertEquals(expectedMap, searchMappingService
        .calculateCenter(new ArrayList<>(Collections.singletonList(internalList1))));
  }

  @Test
  public void testEmptyMap() {
    final String url = "https://nominatim.openstreetmap.org/search/Limburgerhof%20Rewe?format=json";
    assertEquals(new ArrayList<>(), searchMappingService.makeRequest(url));
  }

  @Test
  public void testResultMap() {
//    final String url = "https://nominatim.openstreetmap.org/search/Rewe%20Limburgerhof?format=json";
//    final String url = "https://nominatim.openstreetmap.org/search/Limburgerhof?format=json";
    final String url = "https://nominatim.openstreetmap.org/search/Edeka%20Hochdorf?format=json";
    final List<NominatimResultLocationDto> resultList = searchMappingService.makeRequest(url);
    LOGGER.info(resultList.toString());
    final Map<String, Double> center = searchMappingService.calculateCenter(resultList);
    LOGGER.info(center.toString());
//    assertEquals(49.4251245, center.get("lat"));
//    assertEquals(8.395376, center.get("lon"));
    assert (center.get("lat") > 0);
    assert (center.get("lon") > 0);
  }

  @Test
  public void testNonEmptyResultList() {
    String url = "https://nominatim.openstreetmap.org/search/Mannheim?format=json";
    assertNotEquals("[]", searchMappingService.makeRequest(url).toString());
    LOGGER.info(searchMappingService.makeRequest(url).toString());
//    url = "https://nominatim.openstreetmap.org/search/Rewe?format=json";
//    assertNotEquals("[]", searchMappingService.makeRequest(url).toString());
//    url = "https://nominatim.openstreetmap.org/search/Limburgerhof?format=json";
//    assertNotEquals("[]", searchMappingService.makeRequest(url).toString());
  }
}
