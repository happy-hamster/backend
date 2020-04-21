package de.sakpaas.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Exception that should be used as a base class in order to create new Exceptions throughout the
 * whole application. It is possible to create new exceptions based on extending this class.
 */
public class ApplicationException extends ResponseStatusException {

  private static final long serialVersionUID = 1L;

  @Getter
  private String message;

  @Getter
  private String textId;

  @Getter
  private Object[] parameters;

  @Getter
  private boolean internal;


  /**
   * Creates an ApplicationException with the given values.
   * 
   * @param status the response status that will be send
   * @param message the message that will be in the log
   * @param textId The ID that defines the Text
   * @param internal Whether the error is internal
   * @param replacers replacers for the error message
   */
  public ApplicationException(HttpStatus status, String message, String textId, boolean internal,
      Object... replacers) {
    super(status, message);
    this.message = ErrorAttributes.addParams(message, replacers);
    this.textId = textId;
    this.parameters = replacers;
  }
}
