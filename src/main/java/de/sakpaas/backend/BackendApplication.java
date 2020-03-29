package de.sakpaas.backend;

import de.sakpaas.backend.util.VersionedBeanNameGenerator;
import net.bytebuddy.utility.RandomString;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class BackendApplication {

    public static final String GENERATED = RandomString.make(64);

    public static void main(String[] args) {
        new SpringApplicationBuilder(BackendApplication.class)
                .beanNameGenerator(new VersionedBeanNameGenerator())
                .run(args);
        System.out.println("Database Generation Key: " + GENERATED);
    }
}
