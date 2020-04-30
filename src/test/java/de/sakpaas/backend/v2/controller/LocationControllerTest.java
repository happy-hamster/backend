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
        .andExpect(jsonPath("$.id").value(location.getId()))
        .andExpect(jsonPath("$.name").value(location.getName()))
        // Favorite
        .andExpect(jsonPath("$.favorite").value(false))
        // Coordinates
        .andExpect(jsonPath("$.coordinates.latitude").value(location.getLatitude()))
        .andExpect(jsonPath("$.coordinates.longitude").value(location.getLongitude()))
        // Details
        .andExpect(jsonPath("$.details.type").value(location.getDetails().getType()))
        .andExpect(jsonPath("$.details.brand").value(location.getDetails().getBrand()))
        .andExpect(jsonPath("$.details.openingHours")
            .value(location.getDetails().getOpeningHours()))
        // Occupancy
        .andExpect(jsonPath("$.occupancy.value").doesNotExist())
        .andExpect(jsonPath("$.occupancy.count").value(0))
        .andExpect(jsonPath("$.occupancy.latestReport").doesNotExist())
        // Address
        .andExpect(jsonPath("$.address.country").value(location.getAddress().getCountry()))
        .andExpect(jsonPath("$.address.city").value(location.getAddress().getCity()))
        .andExpect(jsonPath("$.address.postcode")
            .value(location.getAddress().getPostcode()))
        .andExpect(jsonPath("$.address.street").value(location.getAddress().getStreet()))
        .andExpect(jsonPath("$.address.housenumber")
            .value(location.getAddress().getHousenumber()));

    mockMvc.perform(
        get("/v2/locations/1000"))
        // No Authentication
        .andExpect(jsonPath("$.id").value(location.getId()))
        .andExpect(jsonPath("$.name").value(location.getName()))
        // Favorite
        .andExpect(jsonPath("$.favorite").doesNotExist())
        // Coordinates
        .andExpect(jsonPath("$.coordinates.latitude").value(location.getLatitude()))
        .andExpect(jsonPath("$.coordinates.longitude").value(location.getLongitude()))
        // Details
        .andExpect(jsonPath("$.details.type").value(location.getDetails().getType()))
        .andExpect(jsonPath("$.details.brand").value(location.getDetails().getBrand()))
        .andExpect(jsonPath("$.details.openingHours")
            .value(location.getDetails().getOpeningHours()))
        // Occupancy
        .andExpect(jsonPath("$.occupancy.value").doesNotExist())
        .andExpect(jsonPath("$.occupancy.count").value(0))
        .andExpect(jsonPath("$.occupancy.latestReport").doesNotExist())
        // Address
        .andExpect(jsonPath("$.address.country").value(location.getAddress().getCountry()))
        .andExpect(jsonPath("$.address.city").value(location.getAddress().getCity()))
        .andExpect(jsonPath("$.address.postcode")
            .value(location.getAddress().getPostcode()))
        .andExpect(jsonPath("$.address.street").value(location.getAddress().getStreet()))
        .andExpect(jsonPath("$.address.housenumber")
            .value(location.getAddress().getHousenumber()));
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
        .andExpect(jsonPath("$.id").value(location.getId()))
        .andExpect(jsonPath("$.name").value(location.getName()))
        // Favorite
        .andExpect(jsonPath("$.favorite").value(true))
        // Coordinates
        .andExpect(jsonPath("$.coordinates.latitude").value(location.getLatitude()))
        .andExpect(jsonPath("$.coordinates.longitude").value(location.getLongitude()))
        // Details
        .andExpect(jsonPath("$.details.type").value(location.getDetails().getType()))
        .andExpect(jsonPath("$.details.brand").value(location.getDetails().getBrand()))
        .andExpect(jsonPath("$.details.openingHours")
            .value(location.getDetails().getOpeningHours()))
        // Occupancy
        .andExpect(jsonPath("$.occupancy.value").doesNotExist())
        .andExpect(jsonPath("$.occupancy.count").value(0))
        .andExpect(jsonPath("$.occupancy.latestReport").doesNotExist())
        // Address
        .andExpect(jsonPath("$.address.country").value(location.getAddress().getCountry()))
        .andExpect(jsonPath("$.address.city").value(location.getAddress().getCity()))
        .andExpect(jsonPath("$.address.postcode")
            .value(location.getAddress().getPostcode()))
        .andExpect(jsonPath("$.address.street").value(location.getAddress().getStreet()))
        .andExpect(jsonPath("$.address.housenumber")
            .value(location.getAddress().getHousenumber()));

    mockMvc.perform(
        get("/v2/locations/1000"))
        // No Authentication
        .andExpect(jsonPath("$.id").value(location.getId()))
        .andExpect(jsonPath("$.name").value(location.getName()))
        // Favorite
        .andExpect(jsonPath("$.favorite").doesNotExist())
        // Coordinates
        .andExpect(jsonPath("$.coordinates.latitude").value(location.getLatitude()))
        .andExpect(jsonPath("$.coordinates.longitude").value(location.getLongitude()))
        // Details
        .andExpect(jsonPath("$.details.type").value(location.getDetails().getType()))
        .andExpect(jsonPath("$.details.brand").value(location.getDetails().getBrand()))
        .andExpect(jsonPath("$.details.openingHours")
            .value(location.getDetails().getOpeningHours()))
        // Occupancy
        .andExpect(jsonPath("$.occupancy.value").doesNotExist())
        .andExpect(jsonPath("$.occupancy.count").value(0))
        .andExpect(jsonPath("$.occupancy.latestReport").doesNotExist())
        // Address
        .andExpect(jsonPath("$.address.country").value(location.getAddress().getCountry()))
        .andExpect(jsonPath("$.address.city").value(location.getAddress().getCity()))
        .andExpect(jsonPath("$.address.postcode")
            .value(location.getAddress().getPostcode()))
        .andExpect(jsonPath("$.address.street").value(location.getAddress().getStreet()))
        .andExpect(jsonPath("$.address.housenumber")
            .value(location.getAddress().getHousenumber()));
  }

  @Test
  void getByIdOccupancy() throws Exception {
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
        .andExpect(jsonPath("$.id").value(location.getId()))
        .andExpect(jsonPath("$.name").value(location.getName()))
        // Favorite
        .andExpect(jsonPath("$.favorite").value(false))
        // Coordinates
        .andExpect(jsonPath("$.coordinates.latitude").value(location.getLatitude()))
        .andExpect(jsonPath("$.coordinates.longitude").value(location.getLongitude()))
        // Details
        .andExpect(jsonPath("$.details.type").value(location.getDetails().getType()))
        .andExpect(jsonPath("$.details.brand").value(location.getDetails().getBrand()))
        .andExpect(jsonPath("$.details.openingHours")
            .value(location.getDetails().getOpeningHours()))
        // Occupancy
        .andExpect(jsonPath("$.occupancy.value").value(occupancy.getOccupancy()))
        .andExpect(jsonPath("$.occupancy.count").value(1))
        .andExpect(jsonPath("$.occupancy.latestReport").isString())
        // Address
        .andExpect(jsonPath("$.address.country").value(location.getAddress().getCountry()))
        .andExpect(jsonPath("$.address.city").value(location.getAddress().getCity()))
        .andExpect(jsonPath("$.address.postcode")
            .value(location.getAddress().getPostcode()))
        .andExpect(jsonPath("$.address.street").value(location.getAddress().getStreet()))
        .andExpect(jsonPath("$.address.housenumber")
            .value(location.getAddress().getHousenumber()));

    mockMvc.perform(
        get("/v2/locations/1000"))
        // No Authentication
        .andExpect(jsonPath("$.id").value(location.getId()))
        .andExpect(jsonPath("$.name").value(location.getName()))
        // Favorite
        .andExpect(jsonPath("$.favorite").doesNotExist())
        // Coordinates
        .andExpect(jsonPath("$.coordinates.latitude").value(location.getLatitude()))
        .andExpect(jsonPath("$.coordinates.longitude").value(location.getLongitude()))
        // Details
        .andExpect(jsonPath("$.details.type").value(location.getDetails().getType()))
        .andExpect(jsonPath("$.details.brand").value(location.getDetails().getBrand()))
        .andExpect(jsonPath("$.details.openingHours")
            .value(location.getDetails().getOpeningHours()))
        // Occupancy
        .andExpect(jsonPath("$.occupancy.value").value(occupancy.getOccupancy()))
        .andExpect(jsonPath("$.occupancy.count").value(1))
        .andExpect(jsonPath("$.occupancy.latestReport").isString())
        // Address
        .andExpect(jsonPath("$.address.country").value(location.getAddress().getCountry()))
        .andExpect(jsonPath("$.address.city").value(location.getAddress().getCity()))
        .andExpect(jsonPath("$.address.postcode")
            .value(location.getAddress().getPostcode()))
        .andExpect(jsonPath("$.address.street").value(location.getAddress().getStreet()))
        .andExpect(jsonPath("$.address.housenumber")
            .value(location.getAddress().getHousenumber()));
  }
}