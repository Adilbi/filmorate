package com.filmorate.service;

import com.filmorate.exception.NotFoundException;
import com.filmorate.model.Film;
import com.filmorate.model.User;
import com.filmorate.storage.film.FilmStorage;
import com.filmorate.storage.film.InMemoryFilmStorage;
import com.filmorate.storage.user.InMemoryUserStorage;
import com.filmorate.storage.user.UserStorage;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilmServiceTest {
    private FilmStorage filmStorage = new InMemoryFilmStorage();
    private UserStorage userStorage = new InMemoryUserStorage();
    private UserService userService = new UserService(userStorage);
    private FilmService filmService = new FilmService(userService, filmStorage);


    private User createUser(String login) {
        User user = new User();
        user.setLogin(login);
        user.setEmail(login + "@gmail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        return userService.create(user);
    }

    private Film createFilm(String name) {
        Film film = new Film();
        film.setName(name);
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1896, 11, 1));
        film.setDuration(120);
        return filmService.create(film);
    }

    @Test
    void shouldAddLike() {
        Film film = createFilm("A");
        User user = createUser("adilbi");
        filmService.addLike(film.getId(), user.getId());
        assertTrue(film.getLikes().contains(user.getId()));
    }

    @Test
    void shouldNotDuplicateLike() {
        Film film = createFilm("A");
        User user = createUser("adilbi");
        filmService.addLike(film.getId(), user.getId());
        filmService.addLike(film.getId(), user.getId());
        assertEquals(1, film.getLikes().size());
    }

    @Test
    void shouldRemoveLike() {
        Film film = createFilm("A");
        User user = createUser("adilbi");
        filmService.addLike(film.getId(), user.getId());
        filmService.removeLike(film.getId(), user.getId());
        assertTrue(film.getLikes().isEmpty());
    }

    @Test
    void shouldReturnPopularInOrder() {
        Film film = createFilm("A");
        User user = createUser("adilbi");
        Film film2 = createFilm("B");
        User user2 = createUser("kamila");
        filmService.addLike(film.getId(), user.getId());
        filmService.addLike(film2.getId(), user2.getId());
        filmService.addLike(film2.getId(), user.getId());
        List<Film> popular = filmService.getPopular(10);
        assertEquals(film2.getId(), popular.get(0).getId());
        assertEquals(film.getId(), popular.get(1).getId());
    }

    @Test
    void shouldLimitPopularByCount() {
        Film film = createFilm("A");
        Film film2 = createFilm("B");
        Film film3 = createFilm("C");
        assertEquals(2, filmService.getPopular(2).size());

    }

    @Test
    void shouldKeepLikesAfterUpdate() {
        Film film = createFilm("A");
        User user = createUser("adilbi");
        filmService.addLike(film.getId(), user.getId());

        Film updated = new Film();
        updated.setName("Updated");
        updated.setDescription("Description new");
        updated.setReleaseDate(LocalDate.of(1896, 11, 1));
        updated.setDuration(120);
        updated.setId(film.getId());
        filmService.update(updated);
        Film saved = filmService.findById(film.getId());
        assertEquals("Updated", saved.getName());
        assertTrue(saved.getLikes().contains(user.getId()));
    }

    @Test
    void shouldThrowWhenLikeByUnknownUser() {
        Film film = createFilm("A");
        assertThrows(NotFoundException.class, () -> filmService.addLike(film.getId(), 99L));
    }

    @Test
    void shouldThrowWhenLikeUnknownFilm() {
        User user = createUser("adilbi");
        assertThrows(NotFoundException.class, () -> filmService.addLike(99L, user.getId()));
    }
}
