package de.sakpaas.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ApplicationException extends ResponseStatusException {

  private static final long serialVersionUID = 1L;

  private String message;

  public ApplicationException(HttpStatus status, String message) {
    super(status, message);
    this.message = message;
  }

  @Override
  public String getMessage() {
    return this.message;
  }
}
