package de.sakpaas.backend.v2.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.sakpaas.backend.IntegrationTest;
import de.sakpaas.backend.model.Address;
import de.sakpaas.backend.model.Favorite;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.LocationDetails;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Tests the endpoint <code>/v2/users/self/favorites</code> if it conforms to the openAPI
 * specification.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
class EndpointListFavoritesTest extends IntegrationTest {

  private static String ENDPOINT = "/v2/users/self/favorites";

  @Test
  void testEmpty() throws Exception {
    mockMvc.perform(get(ENDPOINT)
        .header("Authorization", AUTHENTICATION_INVALID))
        .andExpect(status().isUnauthorized());

    mockMvc.perform(get(ENDPOINT)
        .header("Authorization", AUTHENTICATION_VALID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$").isEmpty());

    mockMvc.perform(get(ENDPOINT))
        // No Authentication
        .andExpect(status().is4xxClientError());
    // Authentication should be handled by Keycloak, but only the controller is being tested,
    // thus the controller requests the Principal can not be added and a 400 Error is thrown.
    //.andExpect(status().isUnauthorized());
  }

  @Test
  void testFound() throws Exception {
    // Setup test data
    Location locationEdeka = new Location(1000L, "Edeka Eima", 42.0, 7.0,
        new LocationDetails("supermarket", "Mo-Fr 10-22", "Edeka"),
        new Address("DE", "Mannheim", "25565", "Handelshafen", "12a")
    );
    Location locationAldi = new Location(2000L, "Aldi", 42.001, 7.001,
        new LocationDetails("kiosk", "Fr-Sa 12-14", "Aldi"),
        new Address("FR", "Paris", "101010", "Louvre", "1")
    );
    Location locationPenny = new Location(3000L, "Penny", 0.0, 0.0,
        new LocationDetails("beverages", "Sa-So 02-03", "Penny"),
        new Address("CH", "Zurich", "567", "Am Berg", "5")
    );
    super.insert(locationEdeka);
    super.insert(locationAldi);
    super.insert(locationPenny);
    List<Location> locations = Arrays.asList(
        locationEdeka,
        locationAldi,
        locationPenny
    );
    super.insert(new Favorite(USER_UUID, locationEdeka));
    super.insert(new Favorite(USER_UUID, locationPenny));

    // Test all authentication possibilities
    mockMvc.perform(get(ENDPOINT)
        .header("Authorization", AUTHENTICATION_INVALID))
        .andExpect(status().isUnauthorized());

    mockMvc.perform(get(ENDPOINT)
        .header("Authorization", AUTHENTICATION_VALID))
        .andExpect(super.expectLocationList(locations))
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[*].favorite").value(everyItem(equalTo(true))));

    mockMvc.perform(get(ENDPOINT))
        // No Authentication
        .andExpect(status().is4xxClientError());
    // Authentication should be handled by Keycloak, but only the controller is being tested,
    // thus the controller requests the Principal can not be added and a 400 Error is thrown.
    //.andExpect(status().isUnauthorized());
  }
}