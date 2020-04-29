package de.sakpaas.backend.v2.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.sakpaas.backend.IntegrationTest;
import de.sakpaas.backend.service.UserService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessToken;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
class LocationControllerTest extends IntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @SpyBean
  protected UserService userService;

  public static final UUID USER_UUID = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");
  public static final String AUTHENTICATION_VALID = "Bearer token.valid.token";
  public static final String AUTHENTICATION_INVALID = "Bearer token.invalid.token";

  @Test
  void getById() throws Exception {
    AccessToken accessToken = new AccessToken();
    accessToken.setSubject(USER_UUID.toString());
    accessToken.setPreferredUsername("test.user");
    accessToken.setName("Testonius Tester");
    accessToken.setGivenName("Testonius");
    accessToken.setFamilyName("Tester");
    accessToken.setEmail("testonius.tester@example.com");

    Mockito.doAnswer(invocation -> {
      System.out.println((Object) invocation.getArgument(0));
      if (invocation.getArgument(0).equals("token.valid.token"))
        return accessToken;
      throw new VerificationException();
    })
        .when(userService)
        .verifyToken(Mockito.any());

    mockMvc.perform(
        get("/v2/locations/1000")
            .header("Authorization", AUTHENTICATION_INVALID))
        .andExpect(status().isUnauthorized());

    mockMvc.perform(
        get("/v2/locations/1000")
            .header("Authorization", AUTHENTICATION_VALID))
        .andExpect(status().isNotFound());
  }
}