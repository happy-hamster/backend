package de.sakpaas.backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import de.sakpaas.backend.HappyHamsterTest;
import de.sakpaas.backend.dto.UserInfoDto;
import java.security.Principal;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class UserServiceTest extends HappyHamsterTest {

  @Autowired
  UserService userService;


  @Test
  void getUserInfo() {
    String token =
        "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJKNmthTHZGZlVVNHRWV0JZcHM2NHJJMEUyNmh5djc4dTBxdUFnOVJfUnQ4In0.eyJleHAiOjE1ODYyMDgxOTEsImlhdCI6MTU4NjIwNzg5MSwiYXV0aF90aW1lIjoxNTg2MjA2NTQ5LCJqdGkiOiIwNzY2ZTE1ZS1kN2M4LTRiMjAtYjU3OC1kYjg4ZTE2NzQ4MTYiLCJpc3MiOiJodHRwczovL2F1dGguaGFwcHloYW1zdGVyLm9yZy9hdXRoL3JlYWxtcy9kZXYiLCJhdWQiOlsiYnJva2VyIiwiYWNjb3VudCJdLCJzdWIiOiIzYjQzNjU4My1iOTY1LTQzYTMtYWViZS03YzVlY2UzOGQyNjEiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJmcm9udGVuZCIsInNlc3Npb25fc3RhdGUiOiI2MWQ2ZjFmYi0xOWU3LTQzYTEtOGU1MC01YTQyYjBmMDU4OGEiLCJhY3IiOiIwIiwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJicm9rZXIiOnsicm9sZXMiOlsicmVhZC10b2tlbiJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW1lIjoiUm9iZXJ0IEZyYW56a2UiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJyLmwuZnJhbnprZUBnbWFpbC5jb20iLCJnaXZlbl9uYW1lIjoiUm9iZXJ0IiwiZmFtaWx5X25hbWUiOiJGcmFuemtlIiwiZW1haWwiOiJyLmwuZnJhbnprZUBnbWFpbC5jb20ifQ.DcKmnQ8HU2cDDpyawHrqVGoMG8sX6HS3PbwUSCdLGHuW-Nv6Aqf4K0w9Sa_EidBvWxAyA96q1MjgNN1pxWlATc-hR-SRYxKBrbAZASStK6DYh7I1SWjCCt0thEmH1spddzRFPclJeknbL-xKyHB8NwBDlJFaxKPYm73LSBgVHoM22J5t3CyH98jHX-21ZkhoIk8gSdeS86TghON4FONXHuX_cF6FK8OFnn-gYG436WWJxG_jpiCqlU18hlBkZNgrd6Mt_YuiBxWM1yK_hgtfkbJTrkwLqxtSuZhA96W66Jx7X1rycr2vNpO2I4fHNAX_z2Z5R5asUxd_nlm0ilGB-g";

    Principal principal = () -> "3b436583-b965-43a3-aebe-7c5ece38d261";

    UserInfoDto userInfoDto = userService.getUserInfo("Bearer " + token, principal);
    assertThat(userInfoDto.getId()).isEqualTo("3b436583-b965-43a3-aebe-7c5ece38d261");
    assertThat(userInfoDto.getName()).isEqualTo("Robert Franzke");
    assertThat(userInfoDto.getGivenName()).isEqualTo("Robert");
    assertThat(userInfoDto.getFamilyName()).isEqualTo("Franzke");
    assertThat(userInfoDto.getUsername()).isEqualTo("r.l.franzke@gmail.com");
  }
}