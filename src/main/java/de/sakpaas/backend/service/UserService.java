package de.sakpaas.backend.service;

import com.google.common.annotations.VisibleForTesting;
import de.sakpaas.backend.dto.UserInfoDto;
import de.sakpaas.backend.exception.InvalidBearerTokenException;
import de.sakpaas.backend.exception.NoKeycloakDeploymentException;
import de.sakpaas.backend.util.KeycloakConfiguration;
import de.sakpaas.backend.util.TokenUtils;
import java.util.Optional;
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
   * Extracts user information out of the (validated) JWT and returns them as a UserServiceDto,
   * iff the header is not null.
   *
   * @param header Authorization Header from the Request
   * @return UserInformationDto
   * @throws InvalidBearerTokenException If the given Authentication Header is not null and  does
   *                                     not container a valid Bearer Token, this exception will
   *                                     be thrown.
   */
  public Optional<UserInfoDto> getOptionalUserInfo(String header) {
    return (header == null)
        ? Optional.empty()
        : Optional.of(getUserInfo(header));
  }

  /**
   * Extracts User Information out of the (validated) JWT and returns them as a UserServiceDto.
   *
   * @param header Authorization Header from the Request
   * @return UserInformationDto
   * @throws InvalidBearerTokenException If the given Authentication Header does not container a
   *                                     valid Bearer Token, this exception will be thrown.
   */
  public UserInfoDto getUserInfo(String header) throws InvalidBearerTokenException {
    try {
      // Split token from "Bearer token" string
      String token =
          TokenUtils.getTokenFromHeader(header).orElseThrow(InvalidBearerTokenException::new);

      // Validate and parse token
      AccessToken jwt = verifyToken(token);

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

  /**
   * Verifies and parses the given JWT.
   *
   * @param token the JWT to verify and parse
   * @return the parsed {@link AccessToken}
   * @throws VerificationException iff the given JWT is invalid
   */
  @VisibleForTesting
  AccessToken verifyToken(String token) throws VerificationException {
    return AdapterTokenVerifier.verifyToken(
        token,
        keycloakConfiguration.getKeycloakDeployment()
            .orElseThrow(NoKeycloakDeploymentException::new)
    );
  }
}
