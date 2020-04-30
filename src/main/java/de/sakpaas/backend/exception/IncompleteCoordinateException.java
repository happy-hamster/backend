package de.sakpaas.backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception that is used in order to display that a specific Location (ID) does not exist.
 */
public class IncompleteCoordinateException extends ApplicationException {

  private static final long serialVersionUID = 1L;

  private static final String TEXT_ID = "INCOMPLETE_COORDINATES";
  private static final String MESSAGE = "Latitude or Longitude not provided.";

  public IncompleteCoordinateException() {
    super(HttpStatus.BAD_REQUEST, MESSAGE, TEXT_ID, false);
  }
}
