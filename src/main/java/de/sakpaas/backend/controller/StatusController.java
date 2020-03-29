package de.sakpaas.backend.controller;

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

    @Value("${app.commit}")
    private String commit;

    private final MeterRegistry meterRegistry;

    private Counter getCounter;

    public StatusController(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        getCounter = Counter
                .builder("request")
                .description("Total Request since application start on a Endpoint")
                .tags("version", "", "endpoint", "status", "method", "get")
                .register(meterRegistry);
    }


    @RequestMapping("/")
    public StatusDto getApplicationStatus() {
        getCounter.increment();
        StatusDto status = new StatusDto();
        status.setStatus(true);
        status.setVersion(version);
        status.setCommit(commit);
        return status;
    }
}