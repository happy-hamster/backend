package de.sakpaas.backend;

import de.sakpaas.backend.service.UserService;
import java.util.UUID;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessToken;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;

public class IntegrationTest extends HappyHamsterTest {

  @Autowired
  protected MockMvc mockMvc;

  @SpyBean
  protected UserService userService;

  public static final UUID USER_UUID = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");
  public static final AccessToken USER_ACCESS_TOKEN = new AccessToken();
  public static final String AUTHENTICATION_VALID = "Bearer token.valid.token";
  public static final String AUTHENTICATION_INVALID = "Bearer token.invalid.token";

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
      if (invocation.getArgument(0).equals("token.valid.token"))
        return USER_ACCESS_TOKEN;
      throw new VerificationException();
    })
        .when(userService)
        .verifyToken(Mockito.any());
  }
}
