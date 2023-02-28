package ru.yandex.practicum.filmorate.storage.likes;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

@Component
@AllArgsConstructor
@Slf4j
public class LikesDBStorage implements LikesStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmStorage filmStorage;

    @Override
    public Film addLike(int filmId, int userId) {
        String sql = "insert into LIKES(film_id, user_id) values ( ?,? )";
        jdbcTemplate.update(sql, filmId, userId);
        Film film = filmStorage.getFilmById(filmId);
        log.info("Добавлен лайк фильму: {}", film.getName());
        return film;
    }

    @Override
    public Film deleteLike(int filmId, int userId) {
        String sql = "delete FROM likes where film_id = ? and user_id = ? ";
        filmStorage.checkFilmId(userId);
        jdbcTemplate.update(sql, filmId, userId);
        Film film =filmStorage.getFilmById(filmId);
        log.info("Удален лайк у фильма: {}", film.getName());
        return film;
    }
}
