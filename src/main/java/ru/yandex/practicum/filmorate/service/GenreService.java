package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.Collection;
import java.util.Map;

@Service
@AllArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public Collection<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }

    public Collection<Genre> getFilmGenres(int filmId) {
        return genreStorage.getGenresByFilmId(filmId);
    }
    public Genre getGenre(int genreId) {
        return genreStorage.getGenreById(genreId);
    }

    public boolean deleteFilmGenres(int filmId) {
        return genreStorage.deleteFilmGenres(filmId);
    }

    public boolean addFilmGenres(int filmId, Collection<Genre> genres) {
        return genreStorage.addFilmGenres(filmId, genres);
    }

    public Map<Integer, Film> getAllGenresByFilms(Map<Integer, Film> films) {
        return genreStorage.getGenresByFilmIds(films);
    }
}