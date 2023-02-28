package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
public class FilmDBStorage implements FilmStorage{
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;


    @Override
    public Film create(Film film) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("films").usingGeneratedKeyColumns("film_id");
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("duration", film.getDuration())
                .addValue("releasedate", film.getReleaseDate())
                .addValue("rating", film.getMpa().getId());
        Number num = jdbcInsert.executeAndReturnKey(parameters);
        film.setId(num.intValue());

        if (film.getGenres()!= null){
            String sql = "insert into FILMS_GENRE(FILM_ID, GENRE_ID) " +
                    "values ( ?,? )";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sql, film.getId(), genre.getId());
            }
        }
        log.info("Добавлен фильм: {}", film.getName());
        return film;
    }

    @Override
    public Film update(Film film) {
        checkFilmId(film.getId());
        String sql = " update FILMS SET NAME = ?, DESCRIPTION = ?, DURATION = ?, RELEASEDATE = ?," +
                " RATING = ? where FILM_ID = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate(),
                film.getMpa().getId(),
                film.getId()
        );
        genreStorage.updateFilmGenre(film);
        log.info("Обновлен фильм: {}", film.getName());
        return getFilmById(film.getId());
    }

    @Override
    public List<Film> findAllFilms() {
        String sql = "SELECT * FROM FILMS ";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Film(
                rs.getInt("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("releaseDate").toLocalDate(),
                rs.getInt("duration"),
                mpaStorage.getMpaToId(rs.getInt("rating")),
                genreStorage.getGenresByFilmId(rs.getInt("film_id"))
        ));
    }

    @Override
    public List<Film> getFilmsPopular(Integer count) {
        String sql = "select f.film_id, f.name, f.description, f.duration, f.releasedate, " +
                "f.rating, count(l.user_id) as count_films " +
                "from films f left join likes l on f.film_id = l.film_id " +
                "group by f.film_id, f.name, f.description, f.duration, f.releasedate, f.rating " +
                "order by count_films desc " +
                "limit ?";
        return jdbcTemplate.query(sql,(rs, rowNum) -> new Film(
                        rs.getInt("film_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDate("releaseDate").toLocalDate(),
                        rs.getInt("duration"),
                        mpaStorage.getMpaToId(rs.getInt("rating")),
                        genreStorage.getGenresByFilmId(rs.getInt("film_id"))),
                count);
    }

    @Override
    public Film getFilmById(Integer filmId) {
        checkFilmId(filmId);
        String sqlQuery = "select * from films where film_ID = ?";
        Film film = jdbcTemplate.queryForObject(sqlQuery,(rs, rowNum) ->(new Film(
                rs.getInt("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("releasedate").toLocalDate(),
                rs.getInt("duration"),
                mpaStorage.getMpaToId(rs.getInt("rating")),
                genreStorage.getGenresByFilmId(rs.getInt("film_id")))), filmId);
        log.info("Вывод фильма: {}", film.getName());
        return film;
    }

    @Override
    public Film checkFilmId(Integer filmId) {
        SqlRowSet rows = jdbcTemplate.queryForRowSet("select * from FILMS where FILM_ID = ?", filmId);
        if (!rows.next()) throw new NotFoundException("Нет такого фильма");
        return null;
    }
}
