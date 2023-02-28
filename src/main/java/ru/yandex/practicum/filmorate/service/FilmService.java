package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikesStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.List;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Service
@AllArgsConstructor
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final LikesStorage likesStorage;

    public Film create(Film film) {
        validateFilm(film, "Добавлен");
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        validateFilm(film, "Обновлен");
        return filmStorage.update(film);
    }

    public List<Film> findAll() {
        return filmStorage.findAllFilms();
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmStorage.getFilmsPopular(count);
    }

    public Film getFilmById(Integer id) {
        return filmStorage.getFilmById(id);
    }

    public void addLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userService.getUserById(userId);
        likesStorage.addLike(film.getId(), user.getId());
    }

    public void deleteLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userService.getUserById(userId);
        likesStorage.deleteLike(film.getId(), user.getId());
    }

    public void validateFilm(Film film, String text) {
        if (film.getReleaseDate().isBefore( LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше " +  LocalDate.of(1895, 12, 28));
        }
        if (film.getName().isEmpty()||film.getName().isBlank()) {
            log.info("Название фильма не задано");
            throw new ValidationException("Название фильма не задано");
        }
        if (film.getDuration()<0) {
            log.info("Продолжительность фильма не может быть отрицательной");
            throw new ValidationException("Продолжительность фильма не может быть отрицательной");
        }
        log.debug("{} фильм: {}", text, film.getName());
    }
}