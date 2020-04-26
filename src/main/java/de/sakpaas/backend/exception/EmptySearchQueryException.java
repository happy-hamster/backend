package de.sakpaas.backend.exception;

import org.springframework.http.HttpStatus;

public class EmptySearchQueryException extends ApplicationException {
  // TODO Implement Empty Search Query Exception
  public EmptySearchQueryException(long locationId) {
    super(HttpStatus.NOT_FOUND, "to be implemented", "to be implemented", false, locationId);
  }
}
