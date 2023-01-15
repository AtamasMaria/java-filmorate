package ru.yandex.practicum.filmorate.controllers;

import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
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
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            log.warn("Попытка добавить фильм с уже существующим id.");
            throw new ValidationException("There is already such a film.");
        } else {
            validateFilm(film);
            film.setId(++id);
            films.put(film.getId(), film);
            log.debug("Добавлен фильм: {}", film);
            return film;
        }
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            validateFilm(film);
            films.put(film.getId(), film);
            log.debug("Фильм с id = {} был обновлен.", film.getId());
            return film;
        } else {
            log.warn("Попытка изменить фильм с несуществующим id.");
            throw new ValidationException("The movie with this id was not found.");
        }
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isEmpty() || film.getName().isBlank()) {
            log.warn("Попытка добавить фильм без названия.");
            throw new ValidationException("The title of the movie can't be empty.");
        } else if (film.getDescription().length() > 200) {
            log.warn("Попытка добавить фильм с описанием свыше 200 символов");
            throw new ValidationException("The movie description exceeds the maximum number of characters 200");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Попытка создания фильма с датой, ранее 28.12.1895.");
            throw new ValidationException("The release date cannot be earlier than 12/28/1985.");
        } else if (film.getDuration().isNegative()) {
            log.warn("Попытка создания фильма с продолжительностью меньше нуля.");
            throw new ValidationException("The duration of the film cannot be negative.");
        }
    }
}



