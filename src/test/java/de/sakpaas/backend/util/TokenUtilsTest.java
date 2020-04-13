package de.sakpaas.backend.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.sakpaas.backend.PostgresqlContainer;
import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

@RunWith(SpringRunner.class)
@SpringBootTest
class TokenUtilsTest {

  @ClassRule
  private static PostgreSQLContainer postgreSQLContainer = PostgresqlContainer.getInstance();

  @Test
  void getTokenFromHeader() {
    assertThat(TokenUtils.getTokenFromHeader("Bearer SickToken")).isEqualTo("SickToken");

    assertThrows(ArrayIndexOutOfBoundsException.class,
        () -> TokenUtils.getTokenFromHeader("Bearer"));
  }
}