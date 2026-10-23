package com.filmorate.service;

import com.filmorate.exception.NotFoundException;
import com.filmorate.exception.ValidationException;
import com.filmorate.model.Film;
import com.filmorate.storage.film.FilmStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Service
public class FilmService {
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final UserService userService;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(UserService userService, FilmStorage filmStorage) {
        this.userService = userService;
        this.filmStorage = filmStorage;
    }

    private Film getFilmOrThrow(Long id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + id + " не найден"));
    }

    public Film create(Film film) {
        validate(film);
        Film filmCreated = filmStorage.create(film);
        log.info("Created film with id={}", filmCreated.getId());
        return filmCreated;
    }

    public Film update(Film film) {
        validate(film);
        Film oldFilm = getFilmOrThrow(film.getId());
        film.setLikes(oldFilm.getLikes());
        Film filmUpdated = filmStorage.update(film);
        log.info("Updated film with id={}", filmUpdated.getId());
        return filmUpdated;
    }


    public Collection<Film> findAll() {
        return filmStorage.findAll();
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

    public Film findById(Long id) {
        return getFilmOrThrow(id);

    }

    public void addLike(Long filmId, Long userId) {
        Film film = getFilmOrThrow(filmId);
        userService.findById(userId);
        film.getLikes().add(userId);
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = getFilmOrThrow(filmId);
        userService.findById(userId);
        film.getLikes().remove(userId);
    }

    public List<Film> getPopular(int count) {
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
                .limit(count)
                .toList();
    }
}
