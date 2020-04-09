package de.sakpaas.backend.v2.controller;

import de.sakpaas.backend.dto.UserInfoDto;
import de.sakpaas.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v2/user")
public class UserController {

  final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  /**
   * Get Endpoint that returns information of the user specified in the token.
   *
   * @param header The Authorization-Header that has to be provided in the request
   * @return Returns the UserInfoDto
   */
  @GetMapping("/info")
  public ResponseEntity<UserInfoDto> getUserInfo(@RequestHeader("Authorization") String header) {
    return new ResponseEntity<>(userService.getUserInfo(header), HttpStatus.OK);
  }
}
