package de.sakpaas.backend.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.occupancy.report-limits")
public class OccupancyReportLimitsConfiguration {

  private boolean enabled;
  private int locationPeriod;
  private int locationLimit;
  private int globalPeriod;
  private int globalLimit;
}
