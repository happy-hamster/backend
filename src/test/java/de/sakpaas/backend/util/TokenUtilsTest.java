package de.sakpaas.backend.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TokenUtilsTest {

  @Test
  void getTokenFromHeader() {
    assertThat(TokenUtils.getTokenFromHeader("Bearer SickToken")).isEqualTo("SickToken");
  }
}