package de.sakpaas.backend.controller;

import de.sakpaas.backend.dto.StatusDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

  @Value("${app.version}")
  private String version;
  @Value("${app.commit}")
  private String commit;

  public StatusController() {
  }


  @RequestMapping("/")
  public StatusDto getApplicationStatus() {
    StatusDto status = new StatusDto();
    status.setStatus(true);
    status.setVersion(version);
    status.setCommit(commit);
    return status;
  }
}