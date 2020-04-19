package de.sakpaas.backend.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import de.sakpaas.backend.AsyncConfiguration;
import de.sakpaas.backend.HappyHamsterTest;
import de.sakpaas.backend.service.SearchService.SearchResultObject;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
class SearchServiceTest extends HappyHamsterTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(AsyncConfiguration.class);

  @Autowired
  SearchService searchService;

  @Test
  void testSearch() {
    final SearchResultObject resultObject = searchService.search("Ludwigshafen");
    LOGGER.info(resultObject.getLocationList().toString());
    assertTrue(resultObject.getLocationList().size() > 0);
  }
}