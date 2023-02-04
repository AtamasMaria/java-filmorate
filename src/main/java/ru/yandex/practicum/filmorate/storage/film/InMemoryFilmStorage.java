package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.FoundException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private static int id;

    @Override
    public Film create(Film film) {
        if (films.containsKey(film.getId())) {
            log.warn("Попытка добавить фильм с уже существующим id.");
            throw new FoundException(String.format("Фильм с id=%d есть в базе", film.getId()));
        }
        film.setId(++id);
        films.put(film.getId(), film);
        log.debug("Добавлен фильм: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.debug("Фильм с id = {} был обновлен.", film.getId());
            return film;
        } else {
            log.warn("Попытка изменить фильм с несуществующим id.");
            throw new FilmNotFoundException("The movie with this id was not found.");
        }
    }

    @Override
    public List<Film> findAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public List<Film> getFilmsPopular(Integer count) {
        return findAllFilms()
                .stream()
                .filter(film -> film.getLikes() != null)
                .sorted((t1, t2) -> t2.getLikes().size() - t1.getLikes().size())
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
    public void addLike(Integer filmId, Integer userId) {
        films.get(filmId).addLike(userId);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        Film film = films.get(filmId);
        if (!film.getLikes().contains(userId)) {
            throw new NotFoundException("id", String.format(String.format("Фильм с id=%d не найден", id)));
        }
        film.deleteLike(userId);
    }
}
