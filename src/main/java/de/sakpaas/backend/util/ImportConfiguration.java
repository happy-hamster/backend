package de.sakpaas.backend.util;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.import")
public class ImportConfiguration {

  private String country;

  private List<String> shoptypes = new ArrayList<>();

}
