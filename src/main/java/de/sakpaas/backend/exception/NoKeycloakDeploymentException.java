package de.sakpaas.backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception that is used in order to display that the given Bearer Token is invalid.
 */
public class NoKeycloakDeploymentException extends ApplicationException {

  private static final long serialVersionUID = 1L;

  private static final String TEXT_ID = "NO_KEYCLOAK_DEPLOYMENT";
  private static final String MESSAGE =
      "This server has no valid KeyCloak deployment. We can not answer your request.";

  public NoKeycloakDeploymentException() {
    super(HttpStatus.SERVICE_UNAVAILABLE, MESSAGE, TEXT_ID, false);
  }
}
