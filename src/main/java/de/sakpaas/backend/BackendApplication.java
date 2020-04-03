package de.sakpaas.backend;

import de.sakpaas.backend.util.VersionedBeanNameGenerator;
import net.bytebuddy.utility.RandomString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class BackendApplication {

    public static final String GENERATED = RandomString.make(64);
    private static final Logger LOGGER = LoggerFactory.getLogger(BackendApplication.class);

    public static void main(String[] args) {
        new SpringApplicationBuilder(BackendApplication.class)
                .beanNameGenerator(new VersionedBeanNameGenerator())
                .run(args);

        LOGGER.info("=========================================================================================");
        LOGGER.info("Database Generation Key: " + GENERATED);
        LOGGER.info("=========================================================================================");
    }
}
