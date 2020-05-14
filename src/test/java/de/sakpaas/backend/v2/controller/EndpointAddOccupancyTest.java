package de.sakpaas.backend.v2.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import de.sakpaas.backend.IntegrationTest;
import de.sakpaas.backend.model.Address;
import de.sakpaas.backend.model.Favorite;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.LocationDetails;
import de.sakpaas.backend.model.Occupancy;
import de.sakpaas.backend.util.OccupancyReportLimitsConfiguration;
import java.util.List;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Tests the endpoint <code>POST /v2/locations/{id}/occupancy</code> if it conforms to
 * the openAPI specification.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
class EndpointAddOccupancyTest extends IntegrationTest {

  @MockBean
  private OccupancyReportLimitsConfiguration configurationLimits;

  @Test
  void testMalformedLocationId() throws Exception {
    // Test all authentication possibilities
    mockMvc.perform(post("/v2/locations/xxxx/occupancy")
        .header("Authorization", AUTHENTICATION_INVALID)
        .contentType("application/json")
        .content("{\"occupancy\": 0.5,\"clientType\": \"TEST\"}"))
        .andExpect(status().isBadRequest());

    mockMvc.perform(post("/v2/locations/xxxx/occupancy")
        .header("Authorization", AUTHENTICATION_VALID)
        .contentType("application/json")
        .content("{\"occupancy\": 0.5,\"clientType\": \"TEST\"}"))
        .andExpect(status().isBadRequest());

    mockMvc.perform(post("/v2/locations/xxxx/occupancy")
        // No Authentication
        .contentType("application/json")
        .content("{\"occupancy\": 0.5,\"clientType\": \"TEST\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testMalformedOccupancy() throws Exception {
    // Not a number
    // Test all authentication possibilities
    mockMvc.perform(post("/v2/locations/xxxx/occupancy")
        .header("Authorization", AUTHENTICATION_INVALID)
        .contentType("application/json")
        .content("{\"occupancy\": xxxx,\"clientType\": \"TEST\"}"))
        .andExpect(status().isBadRequest());

    mockMvc.perform(post("/v2/locations/xxxx/occupancy")
        .header("Authorization", AUTHENTICATION_VALID)
        .contentType("application/json")
        .content("{\"occupancy\": xxxx,\"clientType\": \"TEST\"}"))
        .andExpect(status().isBadRequest());

    mockMvc.perform(post("/v2/locations/xxxx/occupancy")
        .contentType("application/json")
        .content("{\"occupancy\": xxxx,\"clientType\": \"TEST\"}"))
        // No Authentication
        .andExpect(status().isBadRequest());

    // Negative
    // Test all authentication possibilities
    mockMvc.perform(post("/v2/locations/xxxx/occupancy")
        .header("Authorization", AUTHENTICATION_INVALID)
        .contentType("application/json")
        .content("{\"occupancy\": -0.5,\"clientType\": \"TEST\"}"))
        .andExpect(status().isBadRequest());

    mockMvc.perform(post("/v2/locations/xxxx/occupancy")
        .header("Authorization", AUTHENTICATION_VALID)
        .contentType("application/json")
        .content("{\"occupancy\": -0.5,\"clientType\": \"TEST\"}"))
        .andExpect(status().isBadRequest());

    mockMvc.perform(post("/v2/locations/xxxx/occupancy")
        .contentType("application/json")
        .content("{\"occupancy\": -0.5,\"clientType\": \"TEST\"}"))
        // No Authentication
        .andExpect(status().isBadRequest());

    // Bigger than 1.0
    // Test all authentication possibilities
    mockMvc.perform(post("/v2/locations/xxxx/occupancy")
        .header("Authorization", AUTHENTICATION_INVALID)
        .contentType("application/json")
        .content("{\"occupancy\": 1.5,\"clientType\": \"TEST\"}"))
        .andExpect(status().isBadRequest());

    mockMvc.perform(post("/v2/locations/xxxx/occupancy")
        .header("Authorization", AUTHENTICATION_VALID)
        .contentType("application/json")
        .content("{\"occupancy\": 1.5,\"clientType\": \"TEST\"}"))
        .andExpect(status().isBadRequest());

    mockMvc.perform(post("/v2/locations/xxxx/occupancy")
        .contentType("application/json")
        .content("{\"occupancy\": 1.5,\"clientType\": \"TEST\"}"))
        // No Authentication
        .andExpect(status().isBadRequest());
  }

  @Test
  void testNotFound() throws Exception {
    // Test all authentication possibilities
    mockMvc.perform(post("/v2/locations/1000/occupancy")
        .header("Authorization", AUTHENTICATION_INVALID)
        .contentType("application/json")
        .content("{\"occupancy\": 0.5,\"clientType\": \"TEST\"}"))
        .andExpect(status().isUnauthorized());

    mockMvc.perform(post("/v2/locations/1000/occupancy")
        .header("Authorization", AUTHENTICATION_VALID)
        .contentType("application/json")
        .content("{\"occupancy\": 0.5,\"clientType\": \"TEST\"}"))
        .andExpect(status().isNotFound());

    mockMvc.perform(post("/v2/locations/1000/occupancy")
        // No Authentication
        .contentType("application/json")
        .content("{\"occupancy\": 0.5,\"clientType\": \"TEST\"}"))
        .andExpect(status().isNotFound());
  }

  @Test
  void testFoundAuthenticated() throws Exception {
    // Setup test data
    Location location = new Location(1000L, "Edeka Eima", 42.0, 7.0,
        new LocationDetails("supermarket", "Mo-Fr 10-22", "Edeka"),
        new Address("DE", "Mannheim", "25565", "Handelshafen", "12a")
    );
    super.insert(location);

    // Test all authentication possibilities
    mockMvc.perform(post("/v2/locations/1000/occupancy")
        .header("Authorization", AUTHENTICATION_INVALID)
        .contentType("application/json")
        .content("{\"occupancy\": 0.5,\"clientType\": \"TEST\"}"))
        .andExpect(status().isUnauthorized());

    mockMvc.perform(post("/v2/locations/1000/occupancy")
        .header("Authorization", AUTHENTICATION_VALID)
        .contentType("application/json")
        .content("{\"occupancy\": 0.5,\"clientType\": \"TEST\"}"))
        .andExpect(status().isOk())
        .andExpect(super.expectLocation(location))
        .andExpect(jsonPath("$.occupancy.value").value(0.5))
        .andExpect(jsonPath("$.occupancy.count").value(1))
        .andExpect(jsonPath("$.occupancy.latestReport").isString());

    List<Occupancy> occupancyList = occupancyRepository.findAll();
    assertThat(occupancyList.size()).isEqualTo(1);
    assertThat(occupancyList.get(0).getOccupancy()).isEqualTo(0.5);
    assertThat(occupancyList.get(0).getLocation()).isEqualTo(location);
    assertThat(occupancyList.get(0).getClientType()).isEqualTo("TEST");
    assertThat(occupancyList.get(0).getUserUuid()).isEqualTo(USER_UUID);
  }

  @Test
  void testFoundUnauthenticated() throws Exception {
    // Setup test data
    Location location = new Location(1000L, "Edeka Eima", 42.0, 7.0,
        new LocationDetails("supermarket", "Mo-Fr 10-22", "Edeka"),
        new Address("DE", "Mannheim", "25565", "Handelshafen", "12a")
    );
    super.insert(location);

    mockMvc.perform(post("/v2/locations/1000/occupancy")
        // No Authentication
        .contentType("application/json")
        .content("{\"occupancy\": 0.5,\"clientType\": \"TEST\"}"))
        .andExpect(status().isOk())
        .andExpect(super.expectLocation(location))
        .andExpect(jsonPath("$.occupancy.value").value(0.5))
        .andExpect(jsonPath("$.occupancy.count").value(1))
        .andExpect(jsonPath("$.occupancy.latestReport").isString());

    List<Occupancy> occupancyList = occupancyRepository.findAll();
    assertThat(occupancyList.size()).isEqualTo(1);
    assertThat(occupancyList.get(0).getOccupancy()).isEqualTo(0.5);
    assertThat(occupancyList.get(0).getLocation()).isEqualTo(location);
    assertThat(occupancyList.get(0).getClientType()).isEqualTo("TEST");
    assertThat(occupancyList.get(0).getUserUuid()).isNull();
  }

  @Test
  void testFoundFavorite() throws Exception {
    // Setup test data
    Location location = new Location(1000L, "Edeka Eima", 42.0, 7.0,
        new LocationDetails("supermarket", "Mo-Fr 10-22", "Edeka"),
        new Address("DE", "Mannheim", "25565", "Handelshafen", "12a")
    );
    Favorite favorite = new Favorite(USER_UUID, location);
    super.insert(location);
    super.insert(favorite);

    // Test all authentication possibilities
    mockMvc.perform(post("/v2/locations/1000/occupancy")
        .header("Authorization", AUTHENTICATION_INVALID)
        .contentType("application/json")
        .content("{\"occupancy\": 0.5,\"clientType\": \"TEST\"}"))
        .andExpect(status().isUnauthorized());

    mockMvc.perform(post("/v2/locations/1000/occupancy")
        .header("Authorization", AUTHENTICATION_VALID)
        .contentType("application/json")
        .content("{\"occupancy\": 0.5,\"clientType\": \"TEST\"}"))
        .andExpect(status().isOk())
        .andExpect(super.expectLocation(location))
        .andExpect(jsonPath("$.favorite").value(true))
        .andExpect(jsonPath("$.occupancy.value").value(0.5))
        .andExpect(jsonPath("$.occupancy.count").value(1))
        .andExpect(jsonPath("$.occupancy.latestReport").isString());

    List<Occupancy> occupancyList = occupancyRepository.findAll();
    assertThat(occupancyList.size()).isEqualTo(1);
    assertThat(occupancyList.get(0).getOccupancy()).isEqualTo(0.5);
    assertThat(occupancyList.get(0).getLocation()).isEqualTo(location);
    assertThat(occupancyList.get(0).getClientType()).isEqualTo("TEST");
    assertThat(occupancyList.get(0).getUserUuid()).isEqualTo(USER_UUID);
  }

  @Test
  void testFoundFavoriteUnauthenticated() throws Exception {
    // Setup test data
    Location location = new Location(1000L, "Edeka Eima", 42.0, 7.0,
        new LocationDetails("supermarket", "Mo-Fr 10-22", "Edeka"),
        new Address("DE", "Mannheim", "25565", "Handelshafen", "12a")
    );
    super.insert(location);
    mockMvc.perform(post("/v2/locations/1000/occupancy")
        // No Authentication
        .contentType("application/json")
        .content("{\"occupancy\": 0.5,\"clientType\": \"TEST\"}"))
        .andExpect(status().isOk())
        .andExpect(super.expectLocation(location))
        .andExpect(jsonPath("$.favorite").value(nullValue()))
        .andExpect(jsonPath("$.occupancy.value").value(0.5))
        .andExpect(jsonPath("$.occupancy.count").value(1))
        .andExpect(jsonPath("$.occupancy.latestReport").isString());

    List<Occupancy> occupancyList = occupancyRepository.findAll();
    assertThat(occupancyList.size()).isEqualTo(1);
    assertThat(occupancyList.get(0).getOccupancy()).isEqualTo(0.5);
    assertThat(occupancyList.get(0).getLocation()).isEqualTo(location);
    assertThat(occupancyList.get(0).getClientType()).isEqualTo("TEST");
    assertThat(occupancyList.get(0).getUserUuid()).isNull();
  }

  @Test
  void testReportLimit() throws Exception {
    // Setup test data
    Location location = new Location(1000L, "Edeka Eima", 42.0, 7.0,
        new LocationDetails("supermarket", "Mo-Fr 10-22", "Edeka"),
        new Address("DE", "Mannheim", "25565", "Handelshafen", "12a")
    );
    super.insert(location);
    byte[] requestHash = Hex.decode("fdf2df8a3a1c79f8511a51c83f1529a813ff02a965744c5ebddc11ce3d350cc1");
    Occupancy occupancy = new Occupancy(location, 0.5, "TEST", requestHash);
    Occupancy occupancyUser = new Occupancy(location, 0.5, "TEST", requestHash, USER_UUID);
    super.insert(occupancy);
    super.insert(occupancyUser);

    Mockito.when(configurationLimits.isEnabled()).thenReturn(true);
    Mockito.when(configurationLimits.getLocationPeriod()).thenReturn(5);
    Mockito.when(configurationLimits.getLocationLimit()).thenReturn(1);
    Mockito.when(configurationLimits.getGlobalPeriod()).thenReturn(15);
    Mockito.when(configurationLimits.getGlobalLimit()).thenReturn(4);

    // Check global limits
    Mockito.when(configurationLimits.getGlobalLimit()).thenReturn(0);
    mockMvc.perform(post("/v2/locations/1000/occupancy")
        .header("Authorization", AUTHENTICATION_VALID)
        .header("x-forwarded-for", "8.8.8.8")
        .header("user-agent", "Vivaldi Chromium 24.7.1")
        .contentType("application/json")
        .content("{\"occupancy\": 0.5,\"clientType\": \"TEST\"}"))
        .andExpect(status().isTooManyRequests());

    Mockito.when(configurationLimits.getGlobalLimit()).thenReturn(0);
    mockMvc.perform(post("/v2/locations/1000/occupancy")
        // No Authentication
        .header("x-forwarded-for", "8.8.8.8")
        .header("user-agent", "Vivaldi Chromium 24.7.1")
        .contentType("application/json")
        .content("{\"occupancy\": 0.5,\"clientType\": \"TEST\"}"))
        .andExpect(status().isTooManyRequests());

    Mockito.when(configurationLimits.getGlobalLimit()).thenReturn(4);

    // Check location limits
    Mockito.when(configurationLimits.getLocationLimit()).thenReturn(0);
    mockMvc.perform(post("/v2/locations/1000/occupancy")
        .header("Authorization", AUTHENTICATION_VALID)
        .header("x-forwarded-for", "8.8.8.8")
        .header("user-agent", "Vivaldi Chromium 24.7.1")
        .contentType("application/json")
        .content("{\"occupancy\": 0.5,\"clientType\": \"TEST\"}"))
        .andExpect(status().isTooManyRequests());

    Mockito.when(configurationLimits.getLocationLimit()).thenReturn(0);
    mockMvc.perform(post("/v2/locations/1000/occupancy")
        // No Authentication
        .contentType("application/json")
        .content("{\"occupancy\": 0.5,\"clientType\": \"TEST\"}"))
        .andExpect(status().isTooManyRequests());

    Mockito.when(configurationLimits.getLocationLimit()).thenReturn(1);

    // Check enabled flag
    Mockito.when(configurationLimits.isEnabled()).thenReturn(false);
    Mockito.when(configurationLimits.getGlobalLimit()).thenReturn(0);
    mockMvc.perform(post("/v2/locations/1000/occupancy")
        .header("Authorization", AUTHENTICATION_VALID)
        .header("x-forwarded-for", "8.8.8.8")
        .header("user-agent", "Vivaldi Chromium 24.7.1")
        .contentType("application/json")
        .content("{\"occupancy\": 0.5,\"clientType\": \"TEST\"}"))
        .andExpect(status().isOk());
  }
}