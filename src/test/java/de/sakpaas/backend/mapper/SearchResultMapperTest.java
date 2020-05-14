package de.sakpaas.backend.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


import de.sakpaas.backend.HappyHamsterTest;
import de.sakpaas.backend.model.Address;
import de.sakpaas.backend.model.CoordinateDetails;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.LocationDetails;
import de.sakpaas.backend.model.SearchResultObject;
import de.sakpaas.backend.v2.dto.SearchResultDto;
import de.sakpaas.backend.v2.mapper.SearchResultMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SearchResultMapperTest extends HappyHamsterTest {

  @Autowired
  SearchResultMapper searchResultMapper;

  @Test
  public void testMapToDto() {
    final CoordinateDetails coordinates = new CoordinateDetails(0.0, 0.0);
    final LocationDetails locationDetails = new LocationDetails("type", "openingHours", "brand");
    final Address address = new Address();
    final List<Location> locations = new ArrayList<>(
        Collections.singletonList(new Location(1L, "test", 0.0, 0.0, locationDetails, address)));
    final SearchResultObject searchResultObject =
        new SearchResultObject(coordinates, new HashSet<>(locations));

    final SearchResultDto mappedDto =
        searchResultMapper.mapSearchResultToOutputDto(searchResultObject);

    // check for null
    assertNotNull(mappedDto);

    // check coordinates
    assertEquals(0.0, mappedDto.getCoordinates().getLatitude());
    assertEquals(0.0, mappedDto.getCoordinates().getLongitude());

    // check location
    assertEquals(1, mappedDto.getLocations().size());
    assertEquals(0.0, mappedDto.getLocations().get(0).getCoordinates().getLatitude());
    assertEquals("type", mappedDto.getLocations().get(0).getDetails().getType());
  }
}
