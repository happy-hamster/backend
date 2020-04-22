package de.sakpaas.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.sakpaas.backend.HappyHamsterTest;
import de.sakpaas.backend.dto.NominatimSearchResultListDto;
import de.sakpaas.backend.model.CoordinateDetails;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
class SearchMappingServiceTest extends HappyHamsterTest {

  @Autowired
  private SearchMappingService searchMappingService;

  @Test
  void search() {
    final SearchMappingService mockService = Mockito.spy(searchMappingService);
    final NominatimSearchResultListDto mockedList =
        new NominatimSearchResultListDto(new ArrayList<>(
            Collections
                .singletonList(new NominatimSearchResultListDto.NominatimResultLocationDto(3, 5))));
    Mockito.doReturn(mockedList).when(mockService)
        .makeRequest(Mockito.any());

    final CoordinateDetails result = mockService.search("");
    assertEquals(new CoordinateDetails(3, 5), result);
  }
}