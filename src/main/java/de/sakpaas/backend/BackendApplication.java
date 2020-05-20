package de.sakpaas.backend;

import de.sakpaas.backend.util.VersionedBeanNameGenerator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(nameGenerator = VersionedBeanNameGenerator.class)
public class BackendApplication {

  /**
   * Starts the Spring application and applies all configuration automatically. The database import
   * key will be printed automatically.
   *
   * @param args arguments passed from the command line
   */
  public static void main(String[] args) {
    new SpringApplicationBuilder(BackendApplication.class).run(args);
  }
}
