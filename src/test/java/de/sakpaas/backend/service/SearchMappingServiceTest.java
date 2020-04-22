package de.sakpaas.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.sakpaas.backend.HappyHamsterTest;
import de.sakpaas.backend.dto.NominatimSearchResultListDto;
import de.sakpaas.backend.dto.NominatimSearchResultListDto.NominatimResultLocationDto;
import de.sakpaas.backend.model.CoordinateDetails;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SearchMappingServiceTest extends HappyHamsterTest {

  @Autowired
  @InjectMocks
  private SearchMappingService searchMappingService;


  @Test
  public void testCalcCenter() {
    final NominatimResultLocationDto internalList1 = new NominatimResultLocationDto(3, 5);
    final NominatimResultLocationDto internalList2 = new NominatimResultLocationDto(5, 3);
    final CoordinateDetails coordinateDetails = new CoordinateDetails(4.0, 4.0);
    final CoordinateDetails calculatedCenter = searchMappingService
        .calculateCenter(new NominatimSearchResultListDto(
            new ArrayList<>(Arrays.asList(internalList1, internalList2))));
    assertEquals(coordinateDetails, calculatedCenter);
  }

  @Test
  public void testCalcCenter2() {
    final NominatimResultLocationDto internalList1 =
        new NominatimResultLocationDto(3.123, 5.123);
    final CoordinateDetails coordinateDetails = new CoordinateDetails(3.123, 5.123);

    assertEquals(coordinateDetails, searchMappingService
        .calculateCenter(new NominatimSearchResultListDto(
            new ArrayList<>(Collections.singletonList(internalList1)))));
  }
}
