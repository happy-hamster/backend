package de.sakpaas.backend;

import net.bytebuddy.utility.RandomString;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {
	public static final String GENERATED = RandomString.make(30);

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
		System.out.println("Database Generation Key:" + GENERATED);
	}

}
