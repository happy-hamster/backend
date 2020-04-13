package de.sakpaas.backend.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.sakpaas.backend.HappyHamsterTest;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class TokenUtilsTest extends HappyHamsterTest {

  @Test
  void getTokenFromHeader() {
    assertThat(TokenUtils.getTokenFromHeader("Bearer SickToken")).isEqualTo("SickTokens");

    assertThrows(ArrayIndexOutOfBoundsException.class,
        () -> TokenUtils.getTokenFromHeader("Bearer"));
  }
}