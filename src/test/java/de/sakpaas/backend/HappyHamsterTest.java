package de.sakpaas.backend;

import org.junit.ClassRule;
import org.testcontainers.containers.PostgreSQLContainer;

public class HappyHamsterTest {
  @ClassRule
  protected static PostgreSQLContainer container = PostgresqlContainer.getInstance();
}
