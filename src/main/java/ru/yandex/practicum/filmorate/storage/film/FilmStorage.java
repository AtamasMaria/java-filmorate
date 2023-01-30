package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    Film create(Film film);

    Film update(Film film);

    Collection<Film> getAllFilms();

    List<Film> getPopularFilms(Integer count);

    Film getFilmById(Integer filmId);

    void delete(Film film);
}
