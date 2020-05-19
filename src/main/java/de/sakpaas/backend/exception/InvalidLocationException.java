package de.sakpaas.backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception that is used in order to display that a specific Location (ID) does not exist.
 */
public class InvalidLocationException extends ApplicationException {

  private static final long serialVersionUID = 1L;

  private static final String TEXT_ID = "LOCATION";
  private static final String MESSAGE = "Location with ID %0% does not exist.";

  public InvalidLocationException(long locationId) {
    super(HttpStatus.NOT_FOUND, MESSAGE, TEXT_ID, false, locationId);
  }
}
