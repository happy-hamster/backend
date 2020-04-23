package de.sakpaas.backend.util;

import java.util.Optional;

public class TokenUtils {

  public static final String BEARER_TOKEN_PREFIX = "Bearer ";

  /**
   * Extracts the Token from the Auth-Header.
   *
   * @param header The Authorization-Header
   * @return The JWT
   */
  public static Optional<String> getTokenFromHeader(String header) {
    return header.startsWith(BEARER_TOKEN_PREFIX)
        ? Optional.of(header.substring(BEARER_TOKEN_PREFIX.length()))
        : Optional.empty();
  }
}
