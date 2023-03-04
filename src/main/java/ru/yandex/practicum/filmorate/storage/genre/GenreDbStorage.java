package ru.yandex.practicum.filmorate.storage.genre;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

@Component
@AllArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Genre> getAllGenres() {
        String sql = "SELECT genre_id, name FROM genres ORDER BY genre_id";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Genre(rs.getInt("genre_id"), rs.getString("name")));
    }

    @Override
    public Collection<Genre> getGenresByFilmId(int filmId) {
        String sql = "SELECT g.genre_id, name FROM genres g " +
                "INNER JOIN film_genres fg on g.genre_id = fg.genre_id WHERE film_id=?";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Genre(rs.getInt("genre_id"), rs.getString("name")), filmId);
    }

    @Override
    public Genre getGenreById(int genreId) {
        String sql = "SELECT * FROM genres WHERE genre_id=?";
        Genre genre;
        try {
            genre = jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
                    new Genre(rs.getInt("genre_id"), rs.getString("name")), genreId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Genre", "Жанр не найден.");
        }
        return genre;
    }

    @Override
    public boolean addFilmGenres(int filmId, Collection<Genre> genres) {
        for (Genre genre : genres) {
            String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            jdbcTemplate.update(sql, filmId, genre.getId());
        }
        return true;
    }

    @Override
    public boolean deleteFilmGenres(int filmId) {
        String sql = "DELETE FROM film_genres WHERE film_id=?";
        jdbcTemplate.update(sql, filmId);
        return true;
    }
}
