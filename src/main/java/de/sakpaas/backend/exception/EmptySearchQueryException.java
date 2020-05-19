package de.sakpaas.backend.exception;

import org.springframework.http.HttpStatus;

public class EmptySearchQueryException extends ApplicationException {
  /**
   * Creates an ApplicationException with the given values.
   */
  public EmptySearchQueryException(String message) {
    super(HttpStatus.NOT_FOUND, message, "EMPTY_QUERY", false);
  }
}