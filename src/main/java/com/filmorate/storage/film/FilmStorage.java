package com.filmorate.storage.film;

import com.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;


public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    void delete(Long id);

    Collection<Film> findAll();

    Optional<Film> findById(Long id);
}
