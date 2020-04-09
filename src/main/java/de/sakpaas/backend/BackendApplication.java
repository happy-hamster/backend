package de.sakpaas.backend;

import de.sakpaas.backend.util.VersionedBeanNameGenerator;
import net.bytebuddy.utility.RandomString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(nameGenerator = VersionedBeanNameGenerator.class)
public class BackendApplication {

  public static final String GENERATED = RandomString.make(64);
  private static final Logger LOGGER = LoggerFactory.getLogger(BackendApplication.class);

  /**
   * Starts the Spring application and applies all configuration automatically. The database import
   * key will be printed automatically.
   *
   * @param args arguments passed from the command line
   */
  public static void main(String[] args) {
    new SpringApplicationBuilder(BackendApplication.class).run(args);

    LOGGER.info("===============================================================================");
    LOGGER.info("Database Generation Key: " + GENERATED);
    LOGGER.info("===============================================================================");
  }
}
