package de.sakpaas.backend.v2.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import de.sakpaas.backend.IntegrationTest;
import de.sakpaas.backend.dto.NominatimSearchResultListDto;
import de.sakpaas.backend.model.Address;
import de.sakpaas.backend.model.Favorite;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.LocationDetails;
import de.sakpaas.backend.model.Occupancy;
import de.sakpaas.backend.service.SearchService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

/**
 * Tests the endpoint <code>GET /v2/locations/search/{query}</code> if it conforms to
 * the openAPI specification.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
class EndpointSearchLocationsTest extends IntegrationTest {

  @Autowired
  SearchService searchService;

  @MockBean
  RestTemplate restTemplate;

  @Test
  void testMalformed() throws Exception {
    // Suppress outgoing http call
    mockRestTemplate(null);

    // Test all authentication possibilities
    // Incomplete coordinates given
    mockMvc.perform(get("/v2/locations/search/Mannheim?latitude=42.0")
        .header("Authorization", AUTHENTICATION_INVALID))
        .andExpect(status().isUnauthorized());

    mockMvc.perform(get("/v2/locations/search/Mannheim?latitude=42.0")
        .header("Authorization", AUTHENTICATION_VALID))
        .andExpect(status().isBadRequest());

    mockMvc.perform(get("/v2/locations/search/Mannheim?latitude=42.0"))
        // No Authentication
        .andExpect(status().isBadRequest());

    mockMvc.perform(get("/v2/locations/search/Mannheim?longitude=42.0")
        .header("Authorization", AUTHENTICATION_INVALID))
        .andExpect(status().isUnauthorized());

    mockMvc.perform(get("/v2/locations/search/Mannheim?longitude=42.0")
        .header("Authorization", AUTHENTICATION_VALID))
        .andExpect(status().isBadRequest());

    mockMvc.perform(get("/v2/locations/search/Mannheim?longitude=42.0"))
        // No Authentication
        .andExpect(status().isBadRequest());

    // Wrong type
    mockMvc.perform(get("/v2/locations/search/Mannheim?latitude=XXX&longitude=42.0")
        .header("Authorization", AUTHENTICATION_INVALID))
        .andExpect(status().isBadRequest());

    mockMvc.perform(get("/v2/locations/search/Mannheim?latitude=XXX&longitude=42.0")
        .header("Authorization", AUTHENTICATION_VALID))
        .andExpect(status().isBadRequest());

    mockMvc.perform(get("/v2/locations/search/Mannheim?latitude=XXX&longitude=42.0"))
        // No Authentication
        .andExpect(status().isBadRequest());
  }

  @Test
  void testBrandWithoutCoordinates() throws Exception {
    // Setup test data
    Location locationEdeka = new Location(1000L, "Edeka Eima", 42.0, 7.0,
        new LocationDetails("supermarket", "Mo-Fr 10-22", "Edeka"),
        new Address("DE", "Mannheim", "25565", "Handelshafen", "12a")
    );
    Location locationLidl = new Location(3000L, "Lidl", 0.0, 0.0,
        new LocationDetails("beverages", "Mo-So 01-23", "LIDL"),
        new Address("ES", "Madrid", "5432", "Street", "1111")
    );
    super.insert(locationEdeka);
    super.insert(locationLidl);
    List<Location> locations = Arrays.asList(
        locationEdeka,
        locationLidl
    );
    searchService.updateBrands();
    // Suppress outgoing http call
    mockRestTemplate(null);

    // Test all authentication possibilities
    mockMvc.perform(get("/v2/locations/search/Lidl")
        .header("Authorization", AUTHENTICATION_INVALID))
        .andExpect(status().isUnauthorized());

    mockMvc.perform(get("/v2/locations/search/Lidl")
        .header("Authorization", AUTHENTICATION_VALID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.locations", hasSize(1)))
        .andExpect(jsonPath("$.locations[*].details.brands")
            .value(everyItem(equalTo("lidl"))));

    mockMvc.perform(get("/v2/locations/search/Lidl"))
        // No Authentication
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.locations", hasSize(1)))
        .andExpect(jsonPath("$.locations[*].details.brands")
            .value(everyItem(equalTo("lidl"))));
  }

  @Test
  void testBrandWithCoordinates() throws Exception {
    // Setup test data
    Location locationEdeka = new Location(1000L, "Edeka Eima", 42.0, 7.0,
        new LocationDetails("supermarket", "Mo-Fr 10-22", "Edeka"),
        new Address("DE", "Mannheim", "25565", "Handelshafen", "12a")
    );
    Location locationLidl = new Location(2000L, "Lidl", 42.01, 7.01,
        new LocationDetails("beverages", "Mo-So 01-23", "LIDL"),
        new Address("FR", "Paris", "1235", "Rue", "-7")
    );
    Location locationLidlFaraway = new Location(3000L, "Lidl Faraway", 0.0, 0.0,
        new LocationDetails("beverages", "Mo-So 01-23", "LIDL"),
        new Address("ES", "Madrid", "5432", "Street", "1111")
    );
    super.insert(locationEdeka);
    super.insert(locationLidl);
    super.insert(locationLidlFaraway);
    List<Location> locations = Arrays.asList(
        locationEdeka,
        locationLidl,
        locationLidlFaraway
    );
    searchService.updateBrands();
    // Suppress outgoing http call
    mockRestTemplate(null);

    // Test all authentication possibilities
    mockMvc.perform(get("/v2/locations/search/Lidl?latitude=42.0&longitude=7.0")
        .header("Authorization", AUTHENTICATION_INVALID))
        .andExpect(status().isUnauthorized());

    mockMvc.perform(get("/v2/locations/search/Lidl?latitude=42.0&longitude=7.0")
        .header("Authorization", AUTHENTICATION_VALID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.locations", hasSize(1)))
        .andExpect(jsonPath("$.locations[*].details.brands")
            .value(everyItem(equalTo("lidl"))))
        .andExpect(jsonPath("$.locations[*].id")
            .value(everyItem(equalTo(2000))));

    mockMvc.perform(get("/v2/locations/search/Lidl?latitude=42.0&longitude=7.0"))
        // No Authentication
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.locations", hasSize(1)))
        .andExpect(jsonPath("$.locations[*].details.brands")
            .value(everyItem(equalTo("lidl"))))
        .andExpect(jsonPath("$.locations[*].id")
            .value(everyItem(equalTo(2000))));
  }

  @Test
  void testCityWithoutCoordinates() throws Exception {
    // Setup test data
    Location locationEdeka = new Location(1000L, "Edeka Eima", 42.0, 7.0,
        new LocationDetails("supermarket", "Mo-Fr 10-22", "Edeka"),
        new Address("DE", "Mannheim", "25565", "Handelshafen", "12a")
    );
    Location locationLidl = new Location(3000L, "Lidl", 0.0, 0.0,
        new LocationDetails("beverages", "Mo-So 01-23", "LIDL"),
        new Address("ES", "Madrid", "5432", "Street", "1111")
    );
    super.insert(locationEdeka);
    super.insert(locationLidl);
    List<Location> locations = Arrays.asList(
        locationEdeka,
        locationLidl
    );
    searchService.updateBrands();
    // Suppress outgoing http call
    mockRestTemplate(new NominatimSearchResultListDto(
        Collections.singletonList(
            new NominatimSearchResultListDto.NominatimResultLocationDto(42.0, 7.0)
        )
    ));

    // Test all authentication possibilities
    mockMvc.perform(get("/v2/locations/search/Mannheim")
        .header("Authorization", AUTHENTICATION_INVALID))
        .andExpect(status().isUnauthorized());

    mockMvc.perform(get("/v2/locations/search/Mannheim")
        .header("Authorization", AUTHENTICATION_VALID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.locations", hasSize(1)))
        .andExpect(jsonPath("$.locations[*].address.city")
            .value(everyItem(equalTo("Mannheim"))));

    mockMvc.perform(get("/v2/locations/search/Mannheim"))
        // No Authentication
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.locations", hasSize(1)))
        .andExpect(jsonPath("$.locations[*].address.city")
            .value(everyItem(equalTo("Mannheim"))));
  }

  @Test
  void testCityWithCoordinates() throws Exception {
    // Setup test data
    Location locationEdeka = new Location(1000L, "Edeka Eima", 42.0, 7.0,
        new LocationDetails("supermarket", "Mo-Fr 10-22", "Edeka"),
        new Address("DE", "Mannheim", "25565", "Handelshafen", "12a")
    );
    Location locationLidl = new Location(3000L, "Lidl", 0.0, 0.0,
        new LocationDetails("beverages", "Mo-So 01-23", "LIDL"),
        new Address("ES", "Madrid", "5432", "Street", "1111")
    );
    super.insert(locationEdeka);
    super.insert(locationLidl);
    List<Location> locations = Arrays.asList(
        locationEdeka,
        locationLidl
    );
    searchService.updateBrands();
    // Suppress outgoing http call
    mockRestTemplate(new NominatimSearchResultListDto(
        Collections.singletonList(
            new NominatimSearchResultListDto.NominatimResultLocationDto(42.0, 7.0)
        )
    ));

    // Test all authentication possibilities
    mockMvc.perform(get("/v2/locations/search/Mannheim?latitude=0.0&longitude=0.0")
        .header("Authorization", AUTHENTICATION_INVALID))
        .andExpect(status().isUnauthorized());

    mockMvc.perform(get("/v2/locations/search/Mannheim?latitude=0.0&longitude=0.0")
        .header("Authorization", AUTHENTICATION_VALID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.locations", hasSize(1)))
        .andExpect(jsonPath("$.locations[*].address.city")
            .value(everyItem(equalTo("Mannheim"))));

    mockMvc.perform(get("/v2/locations/search/Mannheim?latitude=0.0&longitude=0.0"))
        // No Authentication
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.locations", hasSize(1)))
        .andExpect(jsonPath("$.locations[*].address.city")
            .value(everyItem(equalTo("Mannheim"))));
  }

  @Test
  void testBrandAndCityWithCoordinates() throws Exception {
    // Setup test data
    Location locationEdeka = new Location(1000L, "Edeka Eima", 42.0, 7.0,
        new LocationDetails("supermarket", "Mo-Fr 10-22", "Edeka"),
        new Address("DE", "Mannheim", "25565", "Handelshafen", "12a")
    );
    Location locationLidl = new Location(2000L, "Lidl", 42.01, 7.01,
        new LocationDetails("beverages", "Mo-So 01-23", "LIDL"),
        new Address("DE", "Mannheim", "25565", "Am Ring", "4711")
    );
    Location locationLidlFaraway = new Location(3000L, "Lidl", 0.0, 0.0,
        new LocationDetails("beverages", "Mo-So 01-23", "LIDL"),
        new Address("ES", "Madrid", "5432", "Street", "1111")
    );
    super.insert(locationEdeka);
    super.insert(locationLidl);
    super.insert(locationLidlFaraway);
    List<Location> locations = Arrays.asList(
        locationEdeka,
        locationLidl,
        locationLidlFaraway
    );
    searchService.updateBrands();
    // Suppress outgoing http call
    mockRestTemplate(new NominatimSearchResultListDto(
        Collections.singletonList(
            new NominatimSearchResultListDto.NominatimResultLocationDto(42.0, 7.0)
        )
    ));

    // Test all authentication possibilities
    mockMvc.perform(get("/v2/locations/search/Lidl Mannheim?latitude=0.0&longitude=0.0")
        .header("Authorization", AUTHENTICATION_INVALID))
        .andExpect(status().isUnauthorized());

    mockMvc.perform(get("/v2/locations/search/Lidl Mannheim?latitude=0.0&longitude=0.0")
        .header("Authorization", AUTHENTICATION_VALID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.locations", hasSize(1)))
        .andExpect(jsonPath("$.locations[*].details.brands")
            .value(everyItem(equalTo("lidl"))))
        .andExpect(jsonPath("$.locations[*].address.city")
            .value(everyItem(equalTo("Mannheim"))))
        .andExpect(jsonPath("$.locations[*].id")
            .value(everyItem(equalTo(2000))));

    mockMvc.perform(get("/v2/locations/search/Lidl Mannheim?latitude=0.0&longitude=0.0"))
        // No Authentication
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.locations", hasSize(1)))
        .andExpect(jsonPath("$.locations[*].details.brands")
            .value(everyItem(equalTo("lidl"))))
        .andExpect(jsonPath("$.locations[*].address.city")
            .value(everyItem(equalTo("Mannheim"))))
        .andExpect(jsonPath("$.locations[*].id")
            .value(everyItem(equalTo(2000))));
  }

  @Test
  void testBrandAndCityWithoutCoordinates() throws Exception {
    // Setup test data
    Location locationEdeka = new Location(1000L, "Edeka Eima", 42.0, 7.0,
        new LocationDetails("supermarket", "Mo-Fr 10-22", "Edeka"),
        new Address("DE", "Mannheim", "25565", "Handelshafen", "12a")
    );
    Location locationLidl = new Location(2000L, "Lidl", 42.01, 7.01,
        new LocationDetails("beverages", "Mo-So 01-23", "LIDL"),
        new Address("DE", "Mannheim", "25565", "Am Ring", "4711")
    );
    Location locationLidlFaraway = new Location(3000L, "Lidl", 0.0, 0.0,
        new LocationDetails("beverages", "Mo-So 01-23", "LIDL"),
        new Address("ES", "Madrid", "5432", "Street", "1111")
    );
    super.insert(locationEdeka);
    super.insert(locationLidl);
    super.insert(locationLidlFaraway);
    List<Location> locations = Arrays.asList(
        locationEdeka,
        locationLidl,
        locationLidlFaraway
    );
    searchService.updateBrands();
    // Suppress outgoing http call
    mockRestTemplate(new NominatimSearchResultListDto(
        Collections.singletonList(
            new NominatimSearchResultListDto.NominatimResultLocationDto(42.0, 7.0)
        )
    ));

    // Test all authentication possibilities
    mockMvc.perform(get("/v2/locations/search/Lidl Mannheim0")
        .header("Authorization", AUTHENTICATION_INVALID))
        .andExpect(status().isUnauthorized());

    mockMvc.perform(get("/v2/locations/search/Lidl Mannheim")
        .header("Authorization", AUTHENTICATION_VALID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.locations", hasSize(1)))
        .andExpect(jsonPath("$.locations[*].details.brands")
            .value(everyItem(equalTo("lidl"))))
        .andExpect(jsonPath("$.locations[*].address.city")
            .value(everyItem(equalTo("Mannheim"))))
        .andExpect(jsonPath("$.locations[*].id")
            .value(everyItem(equalTo(2000))));

    mockMvc.perform(get("/v2/locations/search/Lidl Mannheim"))
        // No Authentication
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.locations", hasSize(1)))
        .andExpect(jsonPath("$.locations[*].details.brands")
            .value(everyItem(equalTo("lidl"))))
        .andExpect(jsonPath("$.locations[*].address.city")
            .value(everyItem(equalTo("Mannheim"))))
        .andExpect(jsonPath("$.locations[*].id")
            .value(everyItem(equalTo(2000))));
  }

  @Test
  void testFavorite() throws Exception {
    // Setup test data
    Location locationEdeka = new Location(1000L, "Edeka Eima", 42.0, 7.0,
        new LocationDetails("supermarket", "Mo-Fr 10-22", "Edeka"),
        new Address("DE", "Mannheim", "25565", "Handelshafen", "12a")
    );
    super.insert(locationEdeka);
    Favorite favorite = new Favorite(USER_UUID, locationEdeka);
    super.insert(favorite);
    searchService.updateBrands();
    // Suppress outgoing http call
    mockRestTemplate(null);

    // Test all authentication possibilities
    mockMvc.perform(get("/v2/locations/search/edeka")
        .header("Authorization", AUTHENTICATION_INVALID))
        .andExpect(status().isUnauthorized());

    mockMvc.perform(get("/v2/locations/search/edeka")
        .header("Authorization", AUTHENTICATION_VALID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.locations", hasSize(1)))
        .andExpect(jsonPath("$.locations[*].details.brands")
            .value(everyItem(equalTo("edeka"))))
        .andExpect(jsonPath("$.locations[*].favorite")
            .value(everyItem(equalTo(true))));

    mockMvc.perform(get("/v2/locations/search/edeka"))
        // No Authentication
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.locations", hasSize(1)))
        .andExpect(jsonPath("$.locations[*].details.brands")
            .value(everyItem(equalTo("edeka"))))
        .andExpect(jsonPath("$.locations[*].favorite")
            .value(everyItem(nullValue())));
  }

  @Test
  void testOccupancy() throws Exception {
    // Setup test data
    Location locationEdeka = new Location(1000L, "Edeka Eima", 42.0, 7.0,
        new LocationDetails("supermarket", "Mo-Fr 10-22", "Edeka"),
        new Address("DE", "Mannheim", "25565", "Handelshafen", "12a")
    );
    super.insert(locationEdeka);
    Occupancy occupancy = new Occupancy(locationEdeka, 0.5, "TEST");
    super.insert(occupancy);
    searchService.updateBrands();
    // Suppress outgoing http call
    mockRestTemplate(null);

    // Test all authentication possibilities
    mockMvc.perform(get("/v2/locations/search/edeka")
        .header("Authorization", AUTHENTICATION_INVALID))
        .andExpect(status().isUnauthorized());

    mockMvc.perform(get("/v2/locations/search/edeka")
        .header("Authorization", AUTHENTICATION_VALID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.locations", hasSize(1)))
        .andExpect(jsonPath("$.locations[*].details.brands")
            .value(everyItem(equalTo("edeka"))))
        .andExpect(jsonPath("$.locations[*].occupancy.value")
            .value(everyItem(equalTo(occupancy.getOccupancy()))))
        .andExpect(jsonPath("$.locations[*].occupancy.count")
            .value(everyItem(equalTo(1))));

    mockMvc.perform(get("/v2/locations/search/edeka"))
        // No Authentication
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.locations", hasSize(1)))
        .andExpect(jsonPath("$.locations[*].details.brands")
            .value(everyItem(equalTo("edeka"))))
        .andExpect(jsonPath("$.locations[*].occupancy.value")
            .value(everyItem(equalTo(occupancy.getOccupancy()))))
        .andExpect(jsonPath("$.locations[*].occupancy.count")
            .value(everyItem(equalTo(1))));
  }

  private void mockRestTemplate(NominatimSearchResultListDto nominatim) {
    Mockito.doReturn(new ResponseEntity<>(nominatim, HttpStatus.OK))
        .when(restTemplate)
        .exchange(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(Class.class));
  }
}