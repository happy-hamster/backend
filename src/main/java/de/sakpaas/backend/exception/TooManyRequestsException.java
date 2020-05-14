package de.sakpaas.backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception that is used in order to display that to many request were send.
 */
public class TooManyRequestsException extends ApplicationException {

  private static final long serialVersionUID = 1L;

  private static final String TEXT_ID = "TOO_MANY_REQUESTS";
  private static final String MESSAGE =
      "You send to many requests.";

  public TooManyRequestsException() {
    super(HttpStatus.TOO_MANY_REQUESTS, MESSAGE, TEXT_ID, false);
  }
}
