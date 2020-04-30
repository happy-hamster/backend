package de.sakpaas.backend;

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
import java.util.UUID;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessToken;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;
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
}
