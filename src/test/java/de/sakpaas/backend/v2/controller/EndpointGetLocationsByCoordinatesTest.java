package de.sakpaas.backend.v2.controller;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.sakpaas.backend.IntegrationTest;
import de.sakpaas.backend.model.Address;
import de.sakpaas.backend.model.Favorite;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.LocationDetails;
import de.sakpaas.backend.model.Occupancy;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Tests the endpoint <code>/v2/locations</code> if it conforms to the openAPI specification.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
class EndpointGetLocationsByCoordinatesTest extends IntegrationTest {

  @Test
  void testMalformed() throws Exception {
    // No coordinates given
    mockMvc.perform(get("/v2/locations")
        .header("Authorization", AUTHENTICATION_INVALID))
        .andExpect(status().isBadRequest());

    mockMvc.perform(get("/v2/locations")
        .header("Authorization", AUTHENTICATION_VALID))
        .andExpect(status().isBadRequest());

    mockMvc.perform(get("/v2/locations"))
        // No Authentication
        .andExpect(status().isBadRequest());

    // Incomplete coordinates given
    mockMvc.perform(get("/v2/locations?latitude=42.0")
        .header("Authorization", AUTHENTICATION_INVALID))
        .andExpect(status().isBadRequest());

    mockMvc.perform(get("/v2/locations?latitude=42.0")
        .header("Authorization", AUTHENTICATION_VALID))
        .andExpect(status().isBadRequest());

    mockMvc.perform(get("/v2/locations?latitude=42.0"))
        // No Authentication
        .andExpect(status().isBadRequest());

    mockMvc.perform(get("/v2/locations?longitude=42.0")
        .header("Authorization", AUTHENTICATION_INVALID))
        .andExpect(status().isBadRequest());

    mockMvc.perform(get("/v2/locations?longitude=42.0")
        .header("Authorization", AUTHENTICATION_VALID))
        .andExpect(status().isBadRequest());

    mockMvc.perform(get("/v2/locations?longitude=42.0"))
        // No Authentication
        .andExpect(status().isBadRequest());

    // Wrong type
    mockMvc.perform(get("/v2/locations?latitude=XXX&longitude=42.0")
        .header("Authorization", AUTHENTICATION_INVALID))
        .andExpect(status().isBadRequest());

    mockMvc.perform(get("/v2/locations?latitude=XXX&longitude=42.0")
        .header("Authorization", AUTHENTICATION_VALID))
        .andExpect(status().isBadRequest());

    mockMvc.perform(get("/v2/locations?latitude=XXX&longitude=42.0"))
        // No Authentication
        .andExpect(status().isBadRequest());
  }

  @Test
  void testEmpty() throws Exception {
    // Setup test data
    Location location = new Location(1000L, "Edeka Eima", 42.0, 7.0,
        new LocationDetails("supermarket", "Mo-Fr 10-22", "Edeka"),
        new Address("DE", "Mannheim", "25565", "Handelshafen", "12a")
    );
    super.insert(location);

    // Test all authentication possibilities
    mockMvc.perform(get("/v2/locations?latitude=0.0&longitude=0.0")
        .header("Authorization", AUTHENTICATION_INVALID))
        .andExpect(status().isUnauthorized());

    mockMvc.perform(get("/v2/locations?latitude=0.0&longitude=0.0")
        .header("Authorization", AUTHENTICATION_VALID))
        .andExpect(jsonPath("$", hasSize(0)));

    mockMvc.perform(get("/v2/locations?latitude=0.0&longitude=0.0"))
        // No Authentication
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  void testFound() throws Exception {
    // Setup test data
    Location locationEdeka = new Location(1000L, "Edeka Eima", 42.0, 7.0,
        new LocationDetails("supermarket", "Mo-Fr 10-22", "Edeka"),
        new Address("DE", "Mannheim", "25565", "Handelshafen", "12a")
    );
    Location locationLidl = new Location(3000L, "Lidl", 0.0, 0.0,
        new LocationDetails("beverages", "Mo-So 01-23", "Lidl"),
        new Address("ES", "Madrid", "5432", "Street", "1111")
    );
    super.insert(locationEdeka);
    super.insert(locationLidl);
    List<Location> locations = Arrays.asList(
        locationEdeka,
        locationLidl
    );

    // Test all authentication possibilities
    mockMvc.perform(get("/v2/locations?latitude=42.0&longitude=7.0")
        .header("Authorization", AUTHENTICATION_INVALID))
        .andExpect(status().isUnauthorized());

    mockMvc.perform(get("/v2/locations?latitude=42.0&longitude=7.0")
        .header("Authorization", AUTHENTICATION_VALID))
        .andExpect(super.expectLocationList(locations))
        .andExpect(jsonPath("$", hasSize(1)));

    mockMvc.perform(get("/v2/locations?latitude=42.0&longitude=7.0"))
        // No Authentication
        .andExpect(super.expectLocationList(locations))
        .andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  void testFavorite() throws Exception {
    // Setup test data
    Location locationEdeka = new Location(1000L, "Edeka Eima", 42.0, 7.0,
        new LocationDetails("supermarket", "Mo-Fr 10-22", "Edeka"),
        new Address("DE", "Mannheim", "25565", "Handelshafen", "12a")
    );
    Location locationAldi = new Location(2000L, "Aldi", 42.001, 7.001,
        new LocationDetails("kiosk", "Fr-Sa 12-14", "Aldi"),
        new Address("FR", "Paris", "101010", "Louvre", "1")
    );
    List<Location> locations = Arrays.asList(
        locationEdeka,
        locationAldi
    );
    super.insert(locationEdeka);
    super.insert(locationAldi);
    Favorite favoriteEdeka = new Favorite(USER_UUID, locationEdeka);
    super.insert(favoriteEdeka);

    // Test all authentication possibilities
    mockMvc.perform(get("/v2/locations?latitude=42.0&longitude=7.0")
        .header("Authorization", AUTHENTICATION_INVALID))
        .andExpect(status().isUnauthorized());

    mockMvc.perform(get("/v2/locations?latitude=42.0&longitude=7.0")
        .header("Authorization", AUTHENTICATION_VALID))
        .andExpect(super.expectLocationList(locations))
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$.[?(@.id==1000)].favorite").value(true))
        .andExpect(jsonPath("$.[?(@.id==2000)].favorite").value(false));

    mockMvc.perform(get("/v2/locations?latitude=42.0&longitude=7.0"))
        // No Authentication
        .andExpect(super.expectLocationList(locations))
        .andExpect(jsonPath("$", hasSize(2)))
        // The result is a list and will not automatically unpacked with nullValue()
        .andExpect(jsonPath("$.[?(@.id==1000)].favorite").value(contains(nullValue())))
        .andExpect(jsonPath("$.[?(@.id==2000)].favorite").value(contains(nullValue())));
  }

  @Test
  void testOccupancy() throws Exception {
    // Setup test data
    Location location = new Location(1000L, "Edeka Eima", 42.0, 7.0,
        new LocationDetails("supermarket", "Mo-Fr 10-22", "Edeka"),
        new Address("DE", "Mannheim", "25565", "Handelshafen", "12a")
    );
    Occupancy occupancy = new Occupancy(location, 0.5, "TEST");
    super.insert(location);
    super.insert(occupancy);

    // Test all authentication possibilities
    mockMvc.perform(
        get("/v2/locations/1000")
            .header("Authorization", AUTHENTICATION_INVALID))
        .andExpect(status().isUnauthorized());

    mockMvc.perform(
        get("/v2/locations/1000")
            .header("Authorization", AUTHENTICATION_VALID))
        .andExpect(super.expectLocation(location))
        .andExpect(jsonPath("$.favorite").value(false))
        .andExpect(jsonPath("$.occupancy.value").value(occupancy.getOccupancy()))
        .andExpect(jsonPath("$.occupancy.count").value(1))
        .andExpect(jsonPath("$.occupancy.latestReport").isString());

    mockMvc.perform(
        get("/v2/locations/1000"))
        // No Authentication
        .andExpect(super.expectLocation(location))
        .andExpect(jsonPath("$.favorite").doesNotExist())
        .andExpect(jsonPath("$.occupancy.value").value(occupancy.getOccupancy()))
        .andExpect(jsonPath("$.occupancy.count").value(1))
        .andExpect(jsonPath("$.occupancy.latestReport").isString());
  }
}