package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    List<Film> findAllFilms();

    List<Film> getFilmsPopular(Integer count);

    Film getFilmById(Integer filmId);

    Film checkFilmId(Integer filmId);
}