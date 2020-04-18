package de.sakpaas.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ApplicationException extends ResponseStatusException {

  private static final long serialVersionUID = 1L;

  @Getter
  private String textId;

  @Getter
  private Object[] replacers;

  public ApplicationException(HttpStatus status, String message, String textId,
      Object... replacers) {
    super(status, message);
    this.textId = textId;
    this.replacers = replacers;
  }
}
