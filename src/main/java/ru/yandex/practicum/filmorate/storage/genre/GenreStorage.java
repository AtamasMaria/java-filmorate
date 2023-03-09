package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import java.util.Collection;
import java.util.Map;

public interface GenreStorage {
    Collection<Genre> getAllGenres();
    Collection<Genre> getGenresByFilmId(int filmId);
    Genre getGenreById(int genreId);
    boolean addFilmGenres(int filmId, Collection<Genre> genres);
    boolean deleteFilmGenres(int filmId);
    public Map<Integer, Film> getGenresByFilmIds(Map<Integer, Film> films);
}