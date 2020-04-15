package de.sakpaas.backend.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.occupancy")
public class OccupancyAccumulationConfiguration {

  private int duration;
  private int constant;

  private double minimum;
  private double factorA;
  private double factorB;
}
