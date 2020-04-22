package de.sakpaas.backend.exception;

import org.springframework.http.HttpStatus;

public class NoSearchResultsException extends ApplicationException {
  /**
   * Creates an ApplicationException with the given values.
   */
  public NoSearchResultsException(String message, String url) {
    super(HttpStatus.NOT_FOUND, message, "NO_SEARCH_RESULTS", false, url);
  }
}
