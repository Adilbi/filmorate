package com.filmorate.controller;
import com.filmorate.exception.ValidationException;
import com.filmorate.model.Film;
import com.filmorate.storage.film.FilmStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;



@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmStorage filmStorage;
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
@Autowired
    public FilmController(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        validate(film);
        filmStorage.create(film);
        log.info("Created film with id={}", film.getId());
        return film;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return new ArrayList<>(filmStorage.findAll());
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        validate(film);
        filmStorage.update(film);
        log.info("Updated film with id={}", film.getId());
        return film;
    }

    private void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым.");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Длина описания максимум 200 символов.");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Длительность должна быть положительной.");
        }

    }
}
