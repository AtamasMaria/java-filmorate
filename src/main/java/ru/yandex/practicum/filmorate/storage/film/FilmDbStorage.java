package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.GenreService;


import java.util.*;

@Component("FilmDbStorage")
@Slf4j
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreService genreService;

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("films").usingGeneratedKeyColumns("film_id");
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("release_date", film.getReleaseDate())
                .addValue("duration", film.getDuration())
                .addValue("mpa_id", film.getMpa().getId());
        Number num = jdbcInsert.executeAndReturnKey(parameters);
        film.setId(num.intValue());


        if (!film.getGenres().isEmpty()) {
            genreService.addFilmGenres(film.getId(), film.getGenres());
        }
        if (film.getLikes() != null) {
            for (Integer userId : film.getLikes()) {
                addLike(film.getId(), userId);
            }
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET name=?, description=?, duration=?, release_date=?," +
                " rate=?, mpa_id=? WHERE film_id=?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate(),
                film.getRate(),
                film.getMpa().getId(),
                film.getId());

        genreService.deleteFilmGenres(film.getId());
        if (!film.getGenres().isEmpty()) {
            genreService.addFilmGenres(film.getId(), film.getGenres());
        }
        if (film.getLikes() != null) {
            for (Integer userId : film.getLikes()) {
                addLike(film.getId(), userId);
            }
        }
        return getFilmById(film.getId());
    }

    @Override
    public List<Film> findAllFilms() {
        String sql = "SELECT * FROM films f INNER JOIN mpa m on f.mpa_id=m.mpa_id";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Film(
                        rs.getInt("film_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        Objects.requireNonNull(rs.getDate("release_date")).toLocalDate(),
                        rs.getInt("duration"),
                        rs.getInt("rate"),
                        new Mpa(rs.getInt("mpa.mpa_id"),
                                rs.getString("mpa.name"),
                                rs.getString("mpa.description")),
                        (List<Genre>) genreService.getFilmGenres(rs.getInt("film_id")),
                        getFilmLikes(rs.getInt("film_id"))));
    }

    @Override
    public List<Film> getFilmsPopular(Integer count) {
        String sql = "SELECT f.film_id FROM films f JOIN likes l ON f.film_id = l.film_id " +
                "GROUP BY f.film_id ORDER BY count(l.user_id) DESC limit ?";

        List<Film> sortedFilmsByLikes = new LinkedList<>(jdbcTemplate.query(sql,
                (rs, rowNum) -> getFilmById(rs.getInt("film_id")), count));

        Collection<Film> filmsWithoutLikes = new ArrayList<>();

        if (sortedFilmsByLikes.size() == 0 || sortedFilmsByLikes.size() < count) {
            int limit = count - sortedFilmsByLikes.size();
            String sql2 = "SELECT film_id FROM films where film_id NOT IN" +
                    " (SELECT film_id FROM likes GROUP BY film_id) limit ?";
            filmsWithoutLikes.addAll(jdbcTemplate.query(sql2,
                    (rs, rowNum) -> getFilmById(rs.getInt("film_id")), limit));
        }
        sortedFilmsByLikes.addAll(filmsWithoutLikes);

        return sortedFilmsByLikes;
    }

    @Override
    public Film getFilmById(Integer filmId) {

        String sqlFilm = "SELECT * FROM films f " +
                "INNER JOIN mpa m ON f.mpa_id=m.mpa_id WHERE f.film_id=?";
        Film film;
        try {
            film = jdbcTemplate.queryForObject(sqlFilm, (rs, rowNum) ->
                            new Film(
                                    rs.getInt("film_id"),
                                    rs.getString("name"),
                                    rs.getString("description"),
                                    Objects.requireNonNull(rs.getDate("release_date")).toLocalDate(),
                                    rs.getInt("duration"),
                                    rs.getInt("rate"),
                                    new Mpa(rs.getInt("mpa.mpa_id"),
                                            rs.getString("mpa.name"),
                                            rs.getString("mpa.description")),
                                    (List<Genre>) genreService.getFilmGenres(rs.getInt("film_id")),
                                    getFilmLikes(rs.getInt("film_id"))),
                    filmId);
        }
        catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Film", "Фильм не был найден.");
        }
        log.info("Найден фильм: {} {}", film.getId(), film.getName());
        return film;
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        String sql = "SELECT * FROM likes WHERE user_id=? AND film_id=?";
        SqlRowSet existLike = jdbcTemplate.queryForRowSet(sql, userId, filmId);
        if (!existLike.next()) {
            String setLike = "INSERT INTO likes (user_id, film_id) VALUES (?, ?) ";
            jdbcTemplate.update(setLike, userId, filmId);
        }
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, userId, filmId);
        log.info(String.valueOf(sqlRowSet.next()));
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        String deleteLike = "DELETE FROM likes WHERE film_id=? AND user_id=?";
        jdbcTemplate.update(deleteLike, filmId, userId);
    }

    private List<Integer> getFilmLikes(int filmId) {
        String sql = "SELECT user_id FROM likes WHERE film_id=?";
        return jdbcTemplate.queryForList(sql, Integer.class, filmId);
    }
}

