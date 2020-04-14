package de.sakpaas.backend.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import de.sakpaas.backend.dto.UserInfoDto;
import de.sakpaas.backend.util.TokenUtils;
import java.security.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

  /**
   * Extracts User Information out of the JWT and returns them as a UserServiceDto.
   *
   * @param header Authorization Header from the Request
   * @return UserInformationDto
   */
  public UserInfoDto getUserInfo(String header, Principal principal) {
    String token = TokenUtils.getTokenFromHeader(header);
    DecodedJWT jwt;
    try {
      jwt = JWT.decode(token);
    } catch (JWTDecodeException e) {
      LOGGER.error("Received an invalid JWT", e);
      throw e;
    }

    return new UserInfoDto(
        principal.getName(),
        jwt.getClaim("preferred_username").asString(),
        jwt.getClaim("name").asString(),
        jwt.getClaim("given_name").asString(),
        jwt.getClaim("family_name").asString(),
        jwt.getClaim("email").asString()
    );
  }
}
