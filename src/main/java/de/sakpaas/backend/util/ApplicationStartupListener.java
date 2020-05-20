package de.sakpaas.backend.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ApplicationStartupListener implements ApplicationListener<ApplicationReadyEvent> {

  @Value("${app.secret}")
  private String secret;

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    log.info("===============================================================================");
    log.info("Database Generation Key: " + secret);
    log.info("===============================================================================");
  }
}
