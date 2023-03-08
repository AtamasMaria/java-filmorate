package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.time.LocalDate;
import java.util.Map;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final GenreService genreService;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage,
                       @Autowired(required = false) UserService userService,
                       GenreService genreService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.genreService = genreService;
    }

    public Film create(Film film) {
        validateReleaseDate(film, "Добавлен");
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        validateReleaseDate(film, "Обновлен");
        return filmStorage.update(film);
    }

    public List<Film> findAll() {
        List<Film> allFilms = filmStorage.findAllFilms();
        Map<Integer, Film> map = new HashMap<>();
        for (Film film : allFilms) {
            map.put(film.getId(), film);
        }
        return new ArrayList<>(genreService.getAllGenresByFilms(map).values());
    }

    public List<Film> getPopularFilms(Integer count) {
        List<Film> popularFilms = filmStorage.getFilmsPopular(count);
        Map<Integer, Film> map = new HashMap<>();
        for (Film film : popularFilms) {
            map.put(film.getId(), film);
        }
        return new ArrayList<>(genreService.getAllGenresByFilms(map).values());
    }

    public Film getFilmById(Integer id) {
        if (id == Integer.MIN_VALUE) {
            throw new NotFoundException("id", "Id не был найден.");
        }
        Film film = filmStorage.getFilmById(id);
        return film;
    }

    public void addLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userService.getUserById(userId);
        filmStorage.addLike(film.getId(), user.getId());
    }

    public void deleteLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userService.getUserById(userId);
        filmStorage.deleteLike(film.getId(), user.getId());
    }

    public void validateReleaseDate(Film film, String text) {
        if (film.getReleaseDate().isBefore( LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше " +  LocalDate.of(1895, 12, 28));
        }
        if (film.getName().isEmpty()||film.getName().isBlank()) {
            throw new ru.yandex.practicum.filmorate.exception.ValidationException("Название фильма не задано");
        }
        if (film.getDuration()<0) {
            throw new ValidationException("Продолжительность фильма не может быть отрицательной");
        }
    }
}