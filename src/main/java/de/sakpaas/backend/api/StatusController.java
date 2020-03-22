package de.sakpaas.backend.api;

import de.sakpaas.backend.dto.StatusDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {
  @RequestMapping("/")
  public StatusDto getApplicationStatus(){
    StatusDto status = new StatusDto();
    status.setStatus(true);
    return status;
  }
}