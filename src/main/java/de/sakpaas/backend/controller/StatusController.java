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

  /**
   * Request endpoint to receive the status and version of the application.
   *
   * <p>
   * This endpoint ("/") has to return a 200 code, as the Google Cloud Engine load balancer needs
   * this answer to rout traffic to this instance.
   * </p>
   *
   * @return the {@link StatusDto}
   */
  @RequestMapping("/")
  public StatusDto getApplicationStatus() {
    StatusDto status = new StatusDto();
    status.setStatus(true);
    status.setVersion(version);
    status.setCommit(commit);
    return status;
  }
}