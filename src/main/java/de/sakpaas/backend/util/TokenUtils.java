package de.sakpaas.backend.util;

public class TokenUtils {

  /**
   * Extracts the Token from the Auth-Header.
   *
   * @param header The Authorization-Header
   * @return The JWT
   */
  public static String getTokenFromHeader(String header) {
    String[] splitted = header.split(" ");
    return splitted[1];
  }
}
