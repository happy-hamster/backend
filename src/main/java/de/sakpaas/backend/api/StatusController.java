package de.sakpaas.backend.api;

import de.sakpaas.backend.dto.StatusDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

  @Value("${app.version}")
  private String version;

  @RequestMapping("/")
  public StatusDto getApplicationStatus(){
    StatusDto status = new StatusDto();
    status.setStatus(true);
    status.setVersion(version);
    return status;
  }
}