package de.sakpaas.backend;

import de.sakpaas.backend.util.KeycloakConfiguration;
import org.junit.ClassRule;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.testcontainers.containers.PostgreSQLContainer;

@EnableConfigurationProperties(KeycloakSpringBootProperties.class)
public class HappyHamsterTest {

  @ClassRule
  public static PostgreSQLContainer container = PostgresqlContainer.getInstance();
  @MockBean
  protected KeycloakConfiguration keycloakConfiguration;
}
