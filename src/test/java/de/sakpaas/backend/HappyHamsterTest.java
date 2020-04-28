package de.sakpaas.backend;

import de.sakpaas.backend.util.KeycloakConfiguration;
import org.junit.ClassRule;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.testcontainers.containers.PostgreSQLContainer;

public class HappyHamsterTest {
  @MockBean
  private KeycloakConfiguration keycloakConfiguration;

  @ClassRule
  public static PostgreSQLContainer container = PostgresqlContainer.getInstance();
}
