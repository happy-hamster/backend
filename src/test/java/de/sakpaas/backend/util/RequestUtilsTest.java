package de.sakpaas.backend.util;

import static org.assertj.core.api.Assertions.assertThat;

import de.sakpaas.backend.HappyHamsterTest;
import javax.servlet.http.HttpServletRequest;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class RequestUtilsTest extends HappyHamsterTest {

  @Test
  void testGenerateConnectionHashForwarded() {
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    Mockito.doReturn("8.8.8.8").when(request).getHeader("X-FORWARDED-FOR");
    Mockito.doReturn("Vivaldi Chromium 24.7.1").when(request).getHeader("User-Agent");

    byte[] result = RequestUtils.getInstance().generateConnectionHash(request);
    byte[] expected =
        Hex.decode("fdf2df8a3a1c79f8511a51c83f1529a813ff02a965744c5ebddc11ce3d350cc1");

    assertThat(result).isEqualTo(expected);
  }

  @Test
  void testGenerateConnectionHash() {
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    Mockito.doReturn(null).when(request).getHeader("X-FORWARDED-FOR");
    Mockito.doReturn("Vivaldi Chromium 24.7.1").when(request).getHeader("User-Agent");
    Mockito.doReturn("4.4.4.4").when(request).getRemoteAddr();

    byte[] result = RequestUtils.getInstance().generateConnectionHash(request);
    byte[] expected =
        Hex.decode("ed804e5d3ed63ea29d3b3fde85e97bec9f58bf6765e56bb54dbe9f13f3820323");

    assertThat(result).isEqualTo(expected);
  }
}