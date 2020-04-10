package de.sakpaas.backend.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class TokenUtilsTest {

  @Test
  void getTokenFromHeader() {
    assertThat(TokenUtils.getTokenFromHeader("Bearer SickToken")).isEqualTo("SickToken");

    assertThrows(ArrayIndexOutOfBoundsException.class,
        () -> TokenUtils.getTokenFromHeader("Bearer"));
  }
}