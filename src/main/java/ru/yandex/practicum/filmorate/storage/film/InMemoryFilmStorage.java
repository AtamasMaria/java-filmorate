package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage{

    private final Map<Integer, Film> films = new HashMap<>();
    private static int id = 0;

    @Override
    public Film create(Film film) {
        if (films.containsKey(film.getId())) {
            log.warn("Попытка добавить фильм с уже существующим id.");
            throw new ValidationException("There is already such a film.");
        } else {
            film.setId(++id);
            films.put(film.getId(), film);
            log.debug("Добавлен фильм: {}", film);
            return film;
        }
    }

    @Override
    public Film update(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.debug("Фильм с id = {} был обновлен.", film.getId());
            return film;
        } else {
            log.warn("Попытка изменить фильм с несуществующим id.");
            throw new ValidationException("The movie with this id was not found.");
        }
    }

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        return getAllFilms()
                .stream()
                .filter(film -> film.getLikesByUsers() != null)
                .sorted((t1, t2) -> t2.getLikesByUsers().size() - t1.getLikesByUsers().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public Film getFilmById(Integer filmId) {
        if (!films.containsKey(filmId)) {
            throw new FilmNotFoundException(String.format("Фильм с id=%d не найден", id));
        }
        return films.get(filmId);
    }

    @Override
    public void delete(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new FilmNotFoundException("Такого фильма не найдено.");
        }
        films.remove(film.getId());
    }
}
