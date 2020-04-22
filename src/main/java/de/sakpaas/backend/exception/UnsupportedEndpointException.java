package de.sakpaas.backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception that is used in order to display that a specific Endpoint is not supported anymore.
 */
public class UnsupportedEndpointException extends ApplicationException {

  /**
   * Creates an {@link UnsupportedEndpointException}.
   */
  public UnsupportedEndpointException() {
    super(HttpStatus.GONE,
        "This endpoint is not supported anymore. Please refer to the openAPI specification.",
        "UNSUPPORTED_ENDPOINT", false);
  }
}
