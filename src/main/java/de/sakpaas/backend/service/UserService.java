package de.sakpaas.backend.service;

import de.sakpaas.backend.dto.UserInfoDto;
import de.sakpaas.backend.exception.InvalidBearerTokenException;
import de.sakpaas.backend.util.KeycloakConfiguration;
import de.sakpaas.backend.util.TokenUtils;
import org.keycloak.adapters.rotation.AdapterTokenVerifier;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  @Autowired
  private KeycloakConfiguration keycloakConfiguration;

  /**
   * Extracts User Information out of the JWT and returns them as a UserServiceDto.
   *
   * @param header Authorization Header from the Request
   * @return UserInformationDto
   * @throws InvalidBearerTokenException If the given Authentication Header does not container a
   *                                     valid Bearer Token, this exception will be thrown.
   */
  public UserInfoDto getUserInfo(String header) throws InvalidBearerTokenException {
    try {
      // Split token from "Bearer token" string
      String token = TokenUtils.getTokenFromHeader(header)
          .orElseThrow(InvalidBearerTokenException::new);

      // Validate and parse token
      AccessToken jwt = AdapterTokenVerifier.verifyToken(
          token,
          keycloakConfiguration.getKeycloakDeployment()
      );

      return new UserInfoDto(
          jwt.getSubject(),
          jwt.getPreferredUsername(),
          jwt.getName(),
          jwt.getGivenName(),
          jwt.getFamilyName(),
          jwt.getEmail()
      );
    } catch (VerificationException e) {
      throw new InvalidBearerTokenException();
    }
  }
}
