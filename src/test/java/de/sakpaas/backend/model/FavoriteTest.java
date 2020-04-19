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

    assertThat(favorite, notNullValue());
    assertThat(favorite.getId(), nullValue());
    assertThat(favorite.getUserUuid(), equalTo(uuid));
    assertThat(favorite.getLocation(), equalTo(location));
  }

  @Test
  public void shouldCreateEmptyFavorite() {
    Favorite favorite = new Favorite();

    assertThat(favorite, notNullValue());
    assertThat(favorite.getId(), nullValue());
    assertThat(favorite.getUserUuid(), nullValue());
    assertThat(favorite.getLocation(), nullValue());
  }
}