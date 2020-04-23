package de.sakpaas.backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import de.sakpaas.backend.HappyHamsterTest;
import de.sakpaas.backend.dto.UserInfoDto;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessToken;
import org.mockito.Mockito;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableConfigurationProperties(KeycloakSpringBootProperties.class)
class UserServiceTest extends HappyHamsterTest {

  @MockBean
  UserService userService;

  @Test
  void getUserInfo() throws VerificationException {
    String token =
        "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJKNmthTHZGZlVVNHRWV0JZcHM2NHJJMEUyNmh5dj"
            + "c4dTBxdUFnOVJfUnQ4In0.eyJleHAiOjE1ODYyMDgxOTEsImlhdCI6MTU4NjIwNzg5MSwiYXV0aF90aW1lIj"
            + "oxNTg2MjA2NTQ5LCJqdGkiOiIwNzY2ZTE1ZS1kN2M4LTRiMjAtYjU3OC1kYjg4ZTE2NzQ4MTYiLCJpc3MiOi"
            + "JodHRwczovL2F1dGguaGFwcHloYW1zdGVyLm9yZy9hdXRoL3JlYWxtcy9kZXYiLCJhdWQiOlsiYnJva2VyIi"
            + "wiYWNjb3VudCJdLCJzdWIiOiIzYjQzNjU4My1iOTY1LTQzYTMtYWViZS03YzVlY2UzOGQyNjEiLCJ0eXAiOi"
            + "JCZWFyZXIiLCJhenAiOiJmcm9udGVuZCIsInNlc3Npb25fc3RhdGUiOiI2MWQ2ZjFmYi0xOWU3LTQzYTEtOG"
            + "U1MC01YTQyYjBmMDU4OGEiLCJhY3IiOiIwIiwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYW"
            + "NjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJicm9rZXIiOnsicm9sZX"
            + "MiOlsicmVhZC10b2tlbiJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS"
            + "1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwiZW1haW"
            + "xfdmVyaWZpZWQiOmZhbHNlLCJuYW1lIjoiUm9iZXJ0IEZyYW56a2UiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOi"
            + "JyLmwuZnJhbnprZUBnbWFpbC5jb20iLCJnaXZlbl9uYW1lIjoiUm9iZXJ0IiwiZmFtaWx5X25hbWUiOiJGcm"
            + "FuemtlIiwiZW1haWwiOiJyLmwuZnJhbnprZUBnbWFpbC5jb20ifQ.DcKmnQ8HU2cDDpyawHrqVGoMG8sX6HS"
            + "3PbwUSCdLGHuW-Nv6Aqf4K0w9Sa_EidBvWxAyA96q1MjgNN1pxWlATc-hR-SRYxKBrbAZASStK6DYh7I1SWj"
            + "CCt0thEmH1spddzRFPclJeknbL-xKyHB8NwBDlJFaxKPYm73LSBgVHoM22J5t3CyH98jHX-21ZkhoIk8gSde"
            + "S86TghON4FONXHuX_cF6FK8OFnn-gYG436WWJxG_jpiCqlU18hlBkZNgrd6Mt_YuiBxWM1yK_hgtfkbJTrkw"
            + "LqxtSuZhA96W66Jx7X1rycr2vNpO2I4fHNAX_z2Z5R5asUxd_nlm0ilGB-g";
    Mockito.when(userService.getUserInfo("Bearer " + token)).thenCallRealMethod();
    Mockito.when(userService.verifyToken("Bearer " + token)).thenReturn(jwt());

    UserInfoDto userInfoDto = userService.getUserInfo("Bearer " + token);
    assertThat(userInfoDto.getId())
        .isEqualTo(UUID.fromString("3b436583-b965-43a3-aebe-7c5ece38d261"));
    assertThat(userInfoDto.getName()).isEqualTo("Robert Franzke");
    assertThat(userInfoDto.getGivenName()).isEqualTo("Robert");
    assertThat(userInfoDto.getFamilyName()).isEqualTo("Franzke");
    assertThat(userInfoDto.getUsername()).isEqualTo("r.l.franzke@gmail.com");
  }

  private AccessToken jwt() {
    AccessToken jwt = new AccessToken();
    jwt.setSubject("3b436583-b965-43a3-aebe-7c5ece38d261");
    jwt.setName("Robert Franzke");
    jwt.setGivenName("Robert");
    jwt.setFamilyName("Franzke");
    jwt.setPreferredUsername("r.l.franzke@gmail.com");
    return jwt;
  }
}