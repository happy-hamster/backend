package de.sakpaas.backend.util;

import lombok.Getter;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.spi.HttpFacade;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class KeycloakConfiguration extends KeycloakSpringBootConfigResolver {
  private final KeycloakDeployment keycloakDeployment;

  public KeycloakConfiguration(
      @Autowired(required = false) KeycloakSpringBootProperties properties) {
    keycloakDeployment = KeycloakDeploymentBuilder.build(properties);
  }

  @Override
  public KeycloakDeployment resolve(HttpFacade.Request facade) {
    return keycloakDeployment;
  }
}
