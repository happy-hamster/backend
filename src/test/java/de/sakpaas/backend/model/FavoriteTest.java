package de.sakpaas.backend.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class FavoriteTest {
  @Test
  public void shouldCreateNewFavorite() {
    Location location = new Location();
    UUID uuid = UUID.randomUUID();
    Favorite favorite = new Favorite(uuid, location);

    assertThat("created Object should not be null", favorite, notNullValue());
    assertThat("created Object should have no Id", favorite.getId(), nullValue());
    assertThat("created Object should have correct UUID", favorite.getUserUuid(), equalTo(uuid));
    assertThat("created Object should have correct Location", favorite.getLocation(),
        equalTo(location));
  }

  @Test
  public void shouldCreateEmptyFavorite() {
    Favorite favorite = new Favorite();

    assertThat("created Object should not be null", favorite, notNullValue());
    assertThat("created Object should be empty", favorite.getId(), nullValue());
    assertThat("created Object should be empty", favorite.getUserUuid(), nullValue());
    assertThat("created Object should be empty", favorite.getLocation(), nullValue());
  }
}