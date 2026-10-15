package com.filmorate.controller;

import com.filmorate.exception.NotFoundException;
import com.filmorate.exception.ValidationException;
import com.filmorate.model.Film;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {
    private FilmController controller = new FilmController();

    private Film validFilm() {
        Film film = new Film();
        film.setName("Бен 10");
        film.setDescription("Секрет Омнитиркса");
        film.setReleaseDate(LocalDate.of(1990, 1, 1));
        film.setDuration(200);
        return film;
    }

    @Test
    void shouldCreateValidFilm() {
        Film film = validFilm();
        Film created = controller.create(film);
        assertEquals(1, created.getId());
    }

    @Test
    void shouldThrowWhenNameIsBlank() {
        Film film = validFilm();
        film.setName(" ");
        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void shouldCreateWhenDescriptionIs200Chars() {
        Film film = validFilm();
        film.setDescription("a".repeat(200));
        assertDoesNotThrow(() -> controller.create(film));
    }

    @Test
    void shouldThrowWhenDescriptionIs201Chars() {
        Film film = validFilm();
        film.setDescription("a".repeat(201));
        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void shouldThrowWhenNameIsEmpty() {
        Film film = validFilm();
        film.setName("");
        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void shouldThrowWhenNameIsNull() {
        Film film = validFilm();
        film.setName(null);
        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void shouldCreateWhenReleaseDateIsMinDate() {
        Film film = validFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        assertDoesNotThrow(() -> controller.create(film));
    }

    @Test
    void shouldThrowWhenReleaseDateBeforeMinDate() {
        Film film = validFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void shouldCreateWhenDurationIsPositive() {
        Film film = validFilm();
        film.setDuration(1);
        assertDoesNotThrow(() -> controller.create(film));
    }

    @Test
    void shouldThrowWhenDurationIsZero() {
        Film film = validFilm();
        film.setDuration(0);
        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void shouldThrowWhenUpdateUnknownFilm() {
        Film film = validFilm();
        film.setId(999L);
        assertThrows(NotFoundException.class, () -> controller.update(film));
    }
}
