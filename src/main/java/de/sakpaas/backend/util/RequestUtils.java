package de.sakpaas.backend.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import javax.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;

/**
 * An utils class for {@link HttpServletRequest} related methods. Using the Singleton approach for
 * better mocking while testing.
 */
public final class RequestUtils {

  private static final RequestUtils INSTANCE = new RequestUtils();

  /**
   * Returns the connection hash for the given {@link HttpServletRequest}.
   *
   * @param request the {@link HttpServletRequest} to hash
   * @return the SHA-256 hash
   */
  @SneakyThrows
  public byte[] generateConnectionHash(HttpServletRequest request) {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");

    // If the backend runs behind a reverse proxy, use the header
    String remoteAddress = request.getHeader("X-FORWARDED-FOR");
    // else use the connecting Address
    if (remoteAddress == null || "".equals(remoteAddress)) {
      remoteAddress = request.getRemoteAddr();
    }

    // Get the user agent
    String userAgent = request.getHeader("User-Agent");

    // Combine the message
    byte[] message = (remoteAddress + '+' + userAgent).getBytes(StandardCharsets.UTF_8);
    return digest.digest(message);
  }

  /**
   * Returns an instance of the {@link RequestUtils} class.
   *
   * @return a {@link RequestUtils} instance
   */
  public static RequestUtils getInstance() {
    return INSTANCE;
  }
}
