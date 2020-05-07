package de.sakpaas.backend.v2.controller;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import de.sakpaas.backend.IntegrationTest;
import de.sakpaas.backend.model.Address;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.LocationDetails;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Tests the endpoint <code>GET /v2/locations/types</code> if it conforms to
 * the openAPI specification.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
class EndpointListLocationTypesTest extends IntegrationTest {

  @Test
  void testEmpty() throws Exception {
    // Test all authentication possibilities
    mockMvc.perform(get("/v2/locations/types")
        .header("Authorization", AUTHENTICATION_INVALID))
        .andExpect(status().is2xxSuccessful());
    // Authentication should be handled by Keycloak, but only the controller is being tested,
    // thus the controller just accepts the request and returns 200.
    //.andExpect(status().isUnauthorized());

    mockMvc.perform(get("/v2/locations/types")
        .header("Authorization", AUTHENTICATION_VALID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));

    mockMvc.perform(get("/v2/locations/types"))
        // No Authentication
        .andExpect(status().isOk())
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

    // Test all authentication possibilities
    mockMvc.perform(get("/v2/locations/types")
        .header("Authorization", AUTHENTICATION_INVALID))
        .andExpect(status().is2xxSuccessful());
    // Authentication should be handled by Keycloak, but only the controller is being tested,
    // thus the controller just accepts the request and returns 200.
    //.andExpect(status().isUnauthorized());

    mockMvc.perform(get("/v2/locations/types")
        .header("Authorization", AUTHENTICATION_VALID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$").value(containsInAnyOrder("supermarket", "beverages")));

    mockMvc.perform(get("/v2/locations/types"))
        // No Authentication
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$").value(containsInAnyOrder("supermarket", "beverages")));
  }
}