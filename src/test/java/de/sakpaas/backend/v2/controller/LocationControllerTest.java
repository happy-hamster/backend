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
import org.springframework.test.web.servlet.ResultMatcher;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
class LocationControllerTest extends IntegrationTest {

  @Test
  void getByIdNotFound() throws Exception {
    mockMvc.perform(
        get("/v2/locations/1000")
            .header("Authorization", AUTHENTICATION_INVALID))
        .andExpect(status().isUnauthorized());

    mockMvc.perform(
        get("/v2/locations/1000")
            .header("Authorization", AUTHENTICATION_VALID))
        .andExpect(status().isNotFound());

    mockMvc.perform(
        get("/v2/locations/1000"))
        // No Authentication
        .andExpect(status().isNotFound());
  }

  @Test
  void getByIdFound() throws Exception {
    // Setup test data
    Location location = new Location(1000L, "Edeka Eima", 42.0, 7.0,
        new LocationDetails("supermarket", "Mo-Fr 10-22", "Edeka"),
        new Address("DE", "Mannheim", "25565", "Handelshafen", "12a")
    );
    super.insert(location);

    // Test all authentication possibilities
    mockMvc.perform(
        get("/v2/locations/1000")
            .header("Authorization", AUTHENTICATION_INVALID))
        .andExpect(status().isUnauthorized());

    mockMvc.perform(
        get("/v2/locations/1000")
            .header("Authorization", AUTHENTICATION_VALID))
        .andExpect(expectSingleLocation(location))
        .andExpect(jsonPath("$.favorite").value(false))
        .andExpect(jsonPath("$.occupancy.value").doesNotExist())
        .andExpect(jsonPath("$.occupancy.count").value(0))
        .andExpect(jsonPath("$.occupancy.latestReport").doesNotExist());

    mockMvc.perform(
        get("/v2/locations/1000"))
        // No Authentication
        .andExpect(expectSingleLocation(location))
        .andExpect(jsonPath("$.favorite").doesNotExist())
        .andExpect(jsonPath("$.occupancy.value").doesNotExist())
        .andExpect(jsonPath("$.occupancy.count").value(0))
        .andExpect(jsonPath("$.occupancy.latestReport").doesNotExist());
  }

  @Test
  void getByIdFoundFavorite() throws Exception {
    // Setup test data
    Location location = new Location(1000L, "Edeka Eima", 42.0, 7.0,
        new LocationDetails("supermarket", "Mo-Fr 10-22", "Edeka"),
        new Address("DE", "Mannheim", "25565", "Handelshafen", "12a")
    );
    Favorite favorite = new Favorite(USER_UUID, location);
    super.insert(location);
    super.insert(favorite);

    // Test all authentication possibilities
    mockMvc.perform(
        get("/v2/locations/1000")
            .header("Authorization", AUTHENTICATION_INVALID))
        .andExpect(status().isUnauthorized());

    mockMvc.perform(
        get("/v2/locations/1000")
            .header("Authorization", AUTHENTICATION_VALID))
        .andExpect(expectSingleLocation(location))
        .andExpect(jsonPath("$.favorite").value(true))
        .andExpect(jsonPath("$.occupancy.value").doesNotExist())
        .andExpect(jsonPath("$.occupancy.count").value(0))
        .andExpect(jsonPath("$.occupancy.latestReport").doesNotExist());

    mockMvc.perform(
        get("/v2/locations/1000"))
        // No Authentication
        .andExpect(expectSingleLocation(location))
        .andExpect(jsonPath("$.favorite").doesNotExist())
        .andExpect(jsonPath("$.occupancy.value").doesNotExist())
        .andExpect(jsonPath("$.occupancy.count").value(0))
        .andExpect(jsonPath("$.occupancy.latestReport").doesNotExist());
  }

  @Test
  void getByIdFoundOccupancy() throws Exception {
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
        .andExpect(expectSingleLocation(location))
        .andExpect(jsonPath("$.favorite").value(false))
        .andExpect(jsonPath("$.occupancy.value").value(occupancy.getOccupancy()))
        .andExpect(jsonPath("$.occupancy.count").value(1))
        .andExpect(jsonPath("$.occupancy.latestReport").isString());

    mockMvc.perform(
        get("/v2/locations/1000"))
        // No Authentication
        .andExpect(expectSingleLocation(location))
        .andExpect(jsonPath("$.favorite").doesNotExist())
        .andExpect(jsonPath("$.occupancy.value").value(occupancy.getOccupancy()))
        .andExpect(jsonPath("$.occupancy.count").value(1))
        .andExpect(jsonPath("$.occupancy.latestReport").isString());
  }

  /**
   * Checks if the given {@link org.springframework.test.web.servlet.MvcResult} has the form of a
   * Location as defined in the openAPI specification. The fields given in the location parameter
   * have to be correct.
   *
   * @param location the baseline {@link Location}
   * @return the {@link ResultMatcher}
   */
  protected ResultMatcher expectSingleLocation(Location location) {
    return result -> {
      ResultMatcher[] matcher = new ResultMatcher[] {
          jsonPath("$.id").value(location.getId()),
          jsonPath("$.name").value(location.getName()),
          // Favorite (unknown contents)
          jsonPath("$.favorite").exists(),
          // Coordinates
          jsonPath("$.coordinates.latitude").value(location.getLatitude()),
          jsonPath("$.coordinates.longitude").value(location.getLongitude()),
          // Details
          jsonPath("$.details.type").value(location.getDetails().getType()),
          jsonPath("$.details.brand").value(location.getDetails().getBrand()),
          jsonPath("$.details.openingHours")
              .value(location.getDetails().getOpeningHours()),
          // Occupancy (unknown contents)
          jsonPath("$.occupancy.value").exists(),
          jsonPath("$.occupancy.count").exists(),
          jsonPath("$.occupancy.latestReport").exists(),
          // Address
          jsonPath("$.address.country").value(location.getAddress().getCountry()),
          jsonPath("$.address.city").value(location.getAddress().getCity()),
          jsonPath("$.address.postcode")
              .value(location.getAddress().getPostcode()),
          jsonPath("$.address.street").value(location.getAddress().getStreet()),
          jsonPath("$.address.housenumber")
              .value(location.getAddress().getHousenumber())
      };
      for (ResultMatcher resultMatcher : matcher) {
        resultMatcher.match(result);
      }
    };
  }
}