package de.sakpaas.backend.service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import de.sakpaas.backend.HappyHamsterTest;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@SpringBootTest
@RunWith(SpringRunner.class)
class ExceptionHandler extends HappyHamsterTest {

  @Autowired
  MockMvc mvc;

  @Test
  public void testingError() {
    try {
      mvc.perform(get("/v2/locations/-999")).andExpect(status().isNotFound());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
