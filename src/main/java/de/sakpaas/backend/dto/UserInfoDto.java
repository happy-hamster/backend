package de.sakpaas.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto {
  private String id;
  private String username;
  private String name;
  private String givenName;
  private String familyName;
  private String mail;
}
