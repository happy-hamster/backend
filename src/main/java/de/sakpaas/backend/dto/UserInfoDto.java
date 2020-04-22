package de.sakpaas.backend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserInfoDto {
  private String id;
  private String username;
  private String name;
  private String givenName;
  private String familyName;
  private String mail;

  /**
   * Constructor for HttpExchange.
   */
  @JsonCreator
  public UserInfoDto(@JsonProperty("id") String id, @JsonProperty("username") String username,
                     @JsonProperty("name") String name, @JsonProperty("givenName") String givenName,
                     @JsonProperty("familyName") String familyName,
                     @JsonProperty("mail") String mail) {
    this.id = id;
    this.username = username;
    this.name = name;
    this.givenName = givenName;
    this.familyName = familyName;
    this.mail = mail;
  }
}
