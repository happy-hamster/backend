package de.sakpaas.backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception that is used in order to display that the given Bearer Token is invalid.
 */
public class InvalidBearerTokenException extends ApplicationException {

  private static final long serialVersionUID = 1L;

  private static final String TEXT_ID = "INVALID_BEARER_TOKEN";
  private static final String MESSAGE = "Your provided Bearer Token is invalid.";

  public InvalidBearerTokenException() {
    super(HttpStatus.UNAUTHORIZED, MESSAGE, TEXT_ID, false);
  }
}
