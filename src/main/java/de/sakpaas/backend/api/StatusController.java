package de.sakpaas.backend.api;

import de.sakpaas.backend.dto.StatusDto;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

  @Value("${app.version}")
  private String version;

  private final MeterRegistry meterRegistry;

  private Counter getCounter;

  public StatusController(MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;

    getCounter = Counter
        .builder("request")
        .description("Total Request since application start on a Endpoint")
        .tags("endpoint", "status", "method", "get")
        .register(meterRegistry);
  }


  @RequestMapping("/")
  public StatusDto getApplicationStatus(){
    getCounter.increment();
    StatusDto status = new StatusDto();
    status.setStatus(true);
    status.setVersion(version);
    return status;
  }
}