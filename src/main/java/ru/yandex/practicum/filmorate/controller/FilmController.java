package ru.yandex.practicum.filmorate.controller;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private Map<Integer, Film> films = new HashMap<>();
    private int id = 0;

    @GetMapping
    public Collection<Film> findAll() {
        log.debug("Количество фильмов: {}", films.size());
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        film.setId(++id);
        if (films.containsKey(film.getId())) {
            log.warn("Попытка добавить фильм с уже существующим id.");
            throw new ValidationException("There is already such a film.");
        } else {
            films.put(film.getId(), film);
            log.debug("Добавлен фильм: {}", film);
            return film;
        }
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.debug("Фильм с id = {} был обновлен.", film.getId());
            return film;
        } else {
            log.warn("Попытка изменить фильм с несуществующим id.");
            throw new ValidationException("The movie with this id was not found.");
        }
    }

}



