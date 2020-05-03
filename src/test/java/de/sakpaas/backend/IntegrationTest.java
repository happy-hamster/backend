package de.sakpaas.backend;

import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import de.sakpaas.backend.model.Favorite;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.Occupancy;
import de.sakpaas.backend.service.AddressRepository;
import de.sakpaas.backend.service.FavoriteRepository;
import de.sakpaas.backend.service.LocationDetailsRepository;
import de.sakpaas.backend.service.LocationRepository;
import de.sakpaas.backend.service.OccupancyRepository;
import de.sakpaas.backend.service.UserService;
import java.util.List;
import java.util.UUID;
import lombok.SneakyThrows;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessToken;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

public class IntegrationTest extends HappyHamsterTest {

  public static final UUID USER_UUID = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");
  public static final AccessToken USER_ACCESS_TOKEN = new AccessToken();
  public static final String AUTHENTICATION_VALID = "Bearer token.valid.token";
  public static final String AUTHENTICATION_INVALID = "Bearer token.invalid.token";
  @Autowired
  protected MockMvc mockMvc;
  @SpyBean
  protected UserService userService;
  @Autowired
  OccupancyRepository occupancyRepository;
  @Autowired
  FavoriteRepository favoriteRepository;
  @Autowired
  LocationRepository locationRepository;
  @Autowired
  LocationDetailsRepository locationDetailsRepository;
  @Autowired
  AddressRepository addressRepository;

  @BeforeAll
  static void setupAll() {
    USER_ACCESS_TOKEN.setSubject(USER_UUID.toString());
    USER_ACCESS_TOKEN.setPreferredUsername("test.user");
    USER_ACCESS_TOKEN.setName("Testonius Tester");
    USER_ACCESS_TOKEN.setGivenName("Testonius");
    USER_ACCESS_TOKEN.setFamilyName("Tester");
    USER_ACCESS_TOKEN.setEmail("testonius.tester@example.com");
  }

  @SneakyThrows
  @BeforeEach
  void setup() {
    Mockito.doAnswer(invocation -> {
      if (invocation.getArgument(0).equals("token.valid.token")) {
        return USER_ACCESS_TOKEN;
      }
      throw new VerificationException();
    })
        .when(userService)
        .verifyToken(Mockito.any());

    // Cleanup tables
    occupancyRepository.deleteAll();
    favoriteRepository.deleteAll();
    locationRepository.deleteAll();
    addressRepository.deleteAll();
    locationDetailsRepository.deleteAll();
  }

  @AfterEach
  void tearDown() {
    // Cleanup tables
    occupancyRepository.deleteAll();
    favoriteRepository.deleteAll();
    locationRepository.deleteAll();
    addressRepository.deleteAll();
    locationDetailsRepository.deleteAll();
  }

  protected void insert(Location location) {
    locationDetailsRepository.save(location.getDetails());
    addressRepository.save(location.getAddress());
    locationRepository.save(location);
  }

  protected void insert(Favorite favorite) {
    favoriteRepository.save(favorite);
  }

  protected void insert(Occupancy occupancy) {
    occupancyRepository.save(occupancy);
  }

  protected ResultMatcher expectErrorObject() {
    return result -> {
      ResultMatcher[] matcher = new ResultMatcher[] {
          jsonPath("$.timestamp").exists(),
          jsonPath("$.status").isNumber(),
          jsonPath("$.error").isString(),
          jsonPath("$.path").isString(),
          jsonPath("$.context.textId").isString(),
          jsonPath("$.context.parameters").isArray(),
          jsonPath("$.context.defaultMessage").isString(),
      };
      for (ResultMatcher resultMatcher : matcher) {
        resultMatcher.match(result);
      }
    };
  }

  /**
   * Checks if the given {@link org.springframework.test.web.servlet.MvcResult} has the form of a
   * Location as defined in the openAPI specification. The fields given in the location parameter
   * have to be correct.
   *
   * @param location the baseline {@link Location}
   * @return the {@link ResultMatcher}
   */
  protected ResultMatcher expectLocation(Location location) {
    return result -> this.match(result, "$", location);
  }


  /**
   * Checks if the given {@link org.springframework.test.web.servlet.MvcResult} has the form of a
   * List of Locations as defined in the openAPI specification. The fields given in the locations
   * parameter have to be correct.
   *
   * @param locations the baseline {@link List} of {@link Location}s
   * @return the {@link ResultMatcher}
   */
  protected ResultMatcher expectLocationList(List<Location> locations) {
    return result -> {
      // Check if the result is an array
      jsonPath("$").isArray().match(result);

      // Find correct Location for array element
      JSONArray array = (JSONArray) JSONValue.parse(result.getResponse().getContentAsString());
      for (int i = 0; i < array.size(); i++) {
        long id = ((JSONObject) array.get(i))
            .getAsNumber("id")
            .longValue();
        Location location = locations.stream()
            .filter(loc -> loc.getId() == id)
            .findAny()
            .orElseThrow(() -> new AssertionError("Unknown LocationId in result."));
        this.match(result, "$[" + i + "]", location);
      }
    };
  }

  private void match(MvcResult result, String selector, Location location) throws Exception {
    // Define assertions
    ResultMatcher[] matcher = new ResultMatcher[] {
        jsonPath(selector + ".id").value(equalTo(location.getId()), Long.class),
        jsonPath(selector + ".name").value(location.getName()),
        // Favorite (unknown contents)
        jsonPath(selector + ".favorite", anything()),
        // Coordinates
        jsonPath(selector + ".coordinates.latitude").value(location.getLatitude()),
        jsonPath(selector + ".coordinates.longitude").value(location.getLongitude()),
        // Details
        jsonPath(selector + ".details.type").value(location.getDetails().getType()),
        jsonPath(selector + ".details.brand").value(location.getDetails().getBrand()),
        jsonPath(selector + ".details.openingHours")
            .value(location.getDetails().getOpeningHours()),
        // Occupancy (unknown contents)
        jsonPath(selector + ".occupancy.value", anything()),
        jsonPath(selector + ".occupancy.count", anything()),
        jsonPath(selector + ".occupancy.latestReport", anything()),
        // Address
        jsonPath(selector + ".address.country").value(location.getAddress().getCountry()),
        jsonPath(selector + ".address.city").value(location.getAddress().getCity()),
        jsonPath(selector + ".address.postcode")
            .value(location.getAddress().getPostcode()),
        jsonPath(selector + ".address.street").value(location.getAddress().getStreet()),
        jsonPath(selector + ".address.housenumber")
            .value(location.getAddress().getHousenumber())
    };
    // Run assertions
    for (ResultMatcher resultMatcher : matcher) {
      resultMatcher.match(result);
    }
  }
}
