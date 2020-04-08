package de.sakpaas.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class UnsupportedEndpointException extends RuntimeException {

  @Override
  public String getMessage() {
    return "This endpoint is no longer supported. Please refer to the openAPI specification.";
  }
}
