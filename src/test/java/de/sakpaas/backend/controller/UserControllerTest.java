package de.sakpaas.backend.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.sakpaas.backend.dto.UserInfoDto;
import de.sakpaas.backend.model.Address;
import de.sakpaas.backend.model.Favorite;
import de.sakpaas.backend.model.Location;
import de.sakpaas.backend.model.LocationDetails;
import de.sakpaas.backend.service.AddressRepository;
import de.sakpaas.backend.service.FavoriteRepository;
import de.sakpaas.backend.service.LocationDetailsRepository;
import de.sakpaas.backend.service.LocationRepository;
import de.sakpaas.backend.service.OccupancyRepository;
import de.sakpaas.backend.service.PresenceRepository;
import de.sakpaas.backend.service.RequestRepository;
import de.sakpaas.backend.service.UserService;
import de.sakpaas.backend.util.KeycloakConfiguration;
import de.sakpaas.backend.v2.controller.UserController;
import de.sakpaas.backend.v2.dto.LocationResultLocationDto;
import de.sakpaas.backend.v2.mapper.LocationMapper;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.common.util.RandomString;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
@EnableConfigurationProperties(KeycloakSpringBootProperties.class)
public class UserControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;
  @MockBean
  private LocationMapper locationMapper;
  @MockBean
  private FavoriteRepository favoriteRepository;
  @MockBean
  private RequestRepository requestRepository;
  @MockBean
  private AddressRepository addressRepository;
  @MockBean
  private LocationDetailsRepository locationDetailsRepository;
  @MockBean
  private LocationRepository locationRepository;
  @MockBean
  private PresenceRepository presenceRepository;
  @MockBean
  private OccupancyRepository occupancyRepository;
  @MockBean
  private MeterRegistry meterRegistry;
  @MockBean
  private KeycloakConfiguration keycloakConfiguration;

  @Test
  public void shouldGetLocationsForCurrentUser() throws Exception {
    String header = RandomString.randomCode(32);
    UUID user = UUID.randomUUID();

    List<Location> locations = new ArrayList<>();
    List<LocationResultLocationDto> resultDtos = new ArrayList<>();
    List<Favorite> favorites = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      Location location = location((long) i);
      locations.add(location);
      resultDtos.add(resultDto((long) i));
      favorites.add(new Favorite(user, location));
    }

    Mockito.when(userService.getUserInfo(header)).thenReturn(
        new UserInfoDto(user.toString(), "test", "test", "test", "test",
            "test@test.de"));
    Mockito.when(favoriteRepository.findByUserUuid(user)).thenReturn(favorites);
    Mockito.when(locationMapper.mapLocationToOutputDto(locations.get(0)))
        .thenReturn(resultDtos.get(0));
    Mockito.when(locationMapper.mapLocationToOutputDto(locations.get(1)))
        .thenReturn(resultDtos.get(1));
    Mockito.when(locationMapper.mapLocationToOutputDto(locations.get(2)))
        .thenReturn(resultDtos.get(2));

    String resultJson =
        this.mockMvc.perform(get("/v2/users/self/favorites").header("Authorization", header))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String compareJson = new ObjectMapper().writeValueAsString(resultDtos);
    assertThat(compareJson, equalTo(resultJson));
  }

  private Location location(Long id) {
    return new Location(id, "Location: " + id, 0.0D, 0.0D, new LocationDetails(), new Address());
  }

  private LocationResultLocationDto resultDto(Long id) {
    return new LocationResultLocationDto(id, "Location: " + id, false, null, null, null, null);
  }

  @Test
  public void postFavorite() {

  }

  @Test
  public void deleteFavorite() {

  }

}
