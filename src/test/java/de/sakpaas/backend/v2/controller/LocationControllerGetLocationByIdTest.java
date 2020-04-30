package de.sakpaas.backend.v2.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.sakpaas.backend.IntegrationTest;
import de.sakpaas.backend.model.Address;
import de.sakpaas.backend.model.Favorite;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.LocationDetails;
import de.sakpaas.backend.model.Occupancy;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Tests the endpoint <code>/v2/locations/{id}</code> if it conforms to the openAPI specification.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
class LocationControllerGetLocationByIdTest extends IntegrationTest {

  @Test
  void getLocationByIdMalformed() throws Exception {
    mockMvc.perform(get("/v2/locations/xxxx")
        .header("Authorization", AUTHENTICATION_INVALID))
        .andExpect(status().isBadRequest());

    mockMvc.perform(get("/v2/locations/xxxx")
        .header("Authorization", AUTHENTICATION_VALID))
        .andExpect(status().isBadRequest());

    mockMvc.perform(get("/v2/locations/xxxx"))
        // No Authentication
        .andExpect(status().isBadRequest());
  }

  @Test
  void getLocationByIdNotFound() throws Exception {
    mockMvc.perform(get("/v2/locations/1000")
        .header("Authorization", AUTHENTICATION_INVALID))
        .andExpect(status().isUnauthorized());

    mockMvc.perform(get("/v2/locations/1000")
        .header("Authorization", AUTHENTICATION_VALID))
        .andExpect(status().isNotFound());

    mockMvc.perform(get("/v2/locations/1000"))
        // No Authentication
        .andExpect(status().isNotFound());
  }

  @Test
  void getLocationByIdFound() throws Exception {
    // Setup test data
    Location location = new Location(1000L, "Edeka Eima", 42.0, 7.0,
        new LocationDetails("supermarket", "Mo-Fr 10-22", "Edeka"),
        new Address("DE", "Mannheim", "25565", "Handelshafen", "12a")
    );
    super.insert(location);

    // Test all authentication possibilities
    mockMvc.perform(get("/v2/locations/1000")
        .header("Authorization", AUTHENTICATION_INVALID))
        .andExpect(status().isUnauthorized());

    mockMvc.perform(get("/v2/locations/1000")
        .header("Authorization", AUTHENTICATION_VALID))
        .andExpect(super.expectLocation(location))
        .andExpect(jsonPath("$.favorite").value(false))
        .andExpect(jsonPath("$.occupancy.value").doesNotExist())
        .andExpect(jsonPath("$.occupancy.count").value(0))
        .andExpect(jsonPath("$.occupancy.latestReport").doesNotExist());

    mockMvc.perform(get("/v2/locations/1000"))
        // No Authentication
        .andExpect(super.expectLocation(location))
        .andExpect(jsonPath("$.favorite").doesNotExist())
        .andExpect(jsonPath("$.occupancy.value").doesNotExist())
        .andExpect(jsonPath("$.occupancy.count").value(0))
        .andExpect(jsonPath("$.occupancy.latestReport").doesNotExist());
  }

  @Test
  void getLocationByIdFoundFavorite() throws Exception {
    // Setup test data
    Location location = new Location(1000L, "Edeka Eima", 42.0, 7.0,
        new LocationDetails("supermarket", "Mo-Fr 10-22", "Edeka"),
        new Address("DE", "Mannheim", "25565", "Handelshafen", "12a")
    );
    Favorite favorite = new Favorite(USER_UUID, location);
    super.insert(location);
    super.insert(favorite);

    // Test all authentication possibilities
    mockMvc.perform(get("/v2/locations/1000")
        .header("Authorization", AUTHENTICATION_INVALID))
        .andExpect(status().isUnauthorized());

    mockMvc.perform(get("/v2/locations/1000")
        .header("Authorization", AUTHENTICATION_VALID))
        .andExpect(super.expectLocation(location))
        .andExpect(jsonPath("$.favorite").value(true))
        .andExpect(jsonPath("$.occupancy.value").doesNotExist())
        .andExpect(jsonPath("$.occupancy.count").value(0))
        .andExpect(jsonPath("$.occupancy.latestReport").doesNotExist());

    mockMvc.perform(get("/v2/locations/1000"))
        // No Authentication
        .andExpect(super.expectLocation(location))
        .andExpect(jsonPath("$.favorite").doesNotExist())
        .andExpect(jsonPath("$.occupancy.value").doesNotExist())
        .andExpect(jsonPath("$.occupancy.count").value(0))
        .andExpect(jsonPath("$.occupancy.latestReport").doesNotExist());
  }

  @Test
  void getLocationByIdFoundOccupancy() throws Exception {
    // Setup test data
    Location location = new Location(1000L, "Edeka Eima", 42.0, 7.0,
        new LocationDetails("supermarket", "Mo-Fr 10-22", "Edeka"),
        new Address("DE", "Mannheim", "25565", "Handelshafen", "12a")
    );
    Occupancy occupancy = new Occupancy(location, 0.5, "TEST");
    super.insert(location);
    super.insert(occupancy);

    // Test all authentication possibilities
    mockMvc.perform(get("/v2/locations/1000")
        .header("Authorization", AUTHENTICATION_INVALID))
        .andExpect(status().isUnauthorized());

    mockMvc.perform(get("/v2/locations/1000")
        .header("Authorization", AUTHENTICATION_VALID))
        .andExpect(super.expectLocation(location))
        .andExpect(jsonPath("$.favorite").value(false))
        .andExpect(jsonPath("$.occupancy.value").value(occupancy.getOccupancy()))
        .andExpect(jsonPath("$.occupancy.count").value(1))
        .andExpect(jsonPath("$.occupancy.latestReport").isString());

    mockMvc.perform(get("/v2/locations/1000"))
        // No Authentication
        .andExpect(super.expectLocation(location))
        .andExpect(jsonPath("$.favorite").doesNotExist())
        .andExpect(jsonPath("$.occupancy.value").value(occupancy.getOccupancy()))
        .andExpect(jsonPath("$.occupancy.count").value(1))
        .andExpect(jsonPath("$.occupancy.latestReport").isString());
  }
}