package de.sakpaas.backend.util;

import java.util.Optional;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.spi.HttpFacade;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfiguration extends KeycloakSpringBootConfigResolver {

  private KeycloakDeployment keycloakDeployment;

  /**
   * Creates a new {@link KeycloakConfiguration} with the {@link KeycloakSpringBootProperties}, if
   * they are available.
   *
   * @param properties the {@link KeycloakSpringBootProperties}
   */
  public KeycloakConfiguration(
      @Autowired(required = false) KeycloakSpringBootProperties properties
  ) {
    keycloakDeployment = (properties != null)
        ? KeycloakDeploymentBuilder.build(properties)
        : null;
  }

  /**
   * Returns the current {@link KeycloakDeployment} if there is one available.
   *
   * @return the current {@link KeycloakDeployment}
   */
  public Optional<KeycloakDeployment> getKeycloakDeployment() {
    return Optional.ofNullable(keycloakDeployment);
  }

  /**
   * Returns the current {@link KeycloakDeployment} for the given {@link HttpFacade.Request}.
   *
   * @param facade the {@link HttpFacade.Request}
   * @return the {@link KeycloakDeployment}
   */
  @Override
  public KeycloakDeployment resolve(HttpFacade.Request facade) {
    return keycloakDeployment;
  }
}
