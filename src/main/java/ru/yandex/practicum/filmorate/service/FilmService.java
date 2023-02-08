package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.List;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Service
@Slf4j
@AllArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    public Film create(Film film) {
        validateReleaseDate(film, "Добавлен");
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        validateReleaseDate(film, "Обновлен");
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
        User user = userService.getUserById(userId);
        filmStorage.addLike(filmId, user.getId());
    }

    public void deleteLike(Integer filmId, Integer userId) {
        User user = userService.getUserById(userId);
        filmStorage.deleteLike(filmId, userId);
    }

    public void validateReleaseDate(Film film, String text) {
        if (film.getReleaseDate().isBefore( LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше " +  LocalDate.of(1895, 12, 28));
        }
        log.debug("{} фильм: {}", text, film.getName());
    }
}