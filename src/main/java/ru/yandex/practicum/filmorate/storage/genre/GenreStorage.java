package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {

    List<Genre> getAllGenres();


    Genre getGenreById(int id);


    List<Genre> getGenresByFilmId(int id);


    void updateFilmGenre(Film film);

    void checkId(int id);
}
