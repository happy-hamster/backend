package de.sakpaas.backend.v2.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.sakpaas.backend.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
class LocationControllerTest extends IntegrationTest {

  @Test
  void getById() throws Exception {
    mockMvc.perform(
        get("/v2/locations/1000")
            .header("Authorization", AUTHENTICATION_INVALID))
        .andExpect(status().isUnauthorized());

    mockMvc.perform(
        get("/v2/locations/1000")
            .header("Authorization", AUTHENTICATION_VALID))
        .andExpect(status().isNotFound());

    mockMvc.perform(
        get("/v2/locations/1000"))
        .andExpect(status().isNotFound());
  }
}