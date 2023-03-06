package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FilmDbStorageTest {

    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void beforeEach(){
        jdbcTemplate.update("DELETE FROM likes");
        jdbcTemplate.update("DELETE FROM film_genres");
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.update("DELETE FROM users");

        jdbcTemplate.update("ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1");
    }

    @Test
    public void testGetFilmById() {

        Film film = new Film(1,
                "film",
                "description",
                LocalDate.of(2000,1,1),
                90,
                new Mpa(1,null,null),
                new ArrayList<>());
        filmDbStorage.create(film);

        Film dbFilm = filmDbStorage.getFilmById(1);
        assertThat(dbFilm).hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    void testGetAllFilms() {
        Film film = new Film(1,
                "film",
                "description",
                LocalDate.of(2000,1,1),
                90,
                new Mpa(1,null,null),
                new ArrayList<>());
        Film film2 = new Film(2,
                "film2",
                "description2",
                LocalDate.of(2000,3,5),
                124,
                new Mpa(4,null,null),
                new ArrayList<>());

        Film filmCreate1 = filmDbStorage.create(film);
        Film filmCreate2 = filmDbStorage.create(film2);

        Collection<Film> dbFilms = filmDbStorage.findAllFilms();
        assertEquals(2, dbFilms.size());
        assertEquals(filmDbStorage.findAllFilms(), List.of(filmCreate1, filmCreate2));
    }

    @Test
    void testUpdateFilm() {
        Film film = new Film(1,
                "film",
                "description",
                LocalDate.of(2000,1,1),
                90,
                new Mpa(1,null,null),
                new ArrayList<>());

        Film film1 = filmDbStorage.create(film);
        film1.setName("update");
        filmDbStorage.update(film1);
        Film dbFilm = filmDbStorage.getFilmById(film1.getId());
        assertThat(dbFilm).hasFieldOrPropertyWithValue("name", "update");
    }

    @Test
    public void testCreateFilmWithSomeId() {
        Film film = new Film(100,
                "film",
                "description",
                LocalDate.of(2000,1,1),
                90,
                new Mpa(1,null,null),
                new ArrayList<>());

        filmDbStorage.create(film);

        assertThat(film).hasFieldOrPropertyWithValue("id", 1);

    }

    @Test
    public void testUpdateFilmWithWrongId() {
        Film film = new Film(100,
                "film",
                "description",
                LocalDate.of(2000,1,1),
                90,
                new Mpa(1,null,null),
                new ArrayList<>());

        FilmNotFoundException e = assertThrows(FilmNotFoundException.class, () -> {
            filmDbStorage.update(film);
        });

        assertEquals(e.getMessage(), "Фильм не был найден.");
    }

    @Test
    public void testAddLike() {
        Film film = new Film(100,
                "film",
                "description",
                LocalDate.of(2000,1,1),
                90,
                new Mpa(1,null,null),
                new ArrayList<>());
        User user = new User(1,
                "ivanov@mail.com",
                "ivanov94",
                "ivanov",
                LocalDate.of(1994, 3, 28));
        Film film1 = filmDbStorage.create(film);
        User user1 = userDbStorage.create(user);

        filmDbStorage.addLike(film1.getId(), user1.getId());
        Boolean bool = jdbcTemplate.queryForObject("select exists " +
                        "(select * from likes where film_id = ? and user_id = ?)",
                Boolean.class, film.getId(), user.getId());

        assertTrue(bool);

    }

    @Test
    public void testDeleteLike() {
        Film film = new Film(100,
                "film",
                "description",
                LocalDate.of(2000,1,1),
                90,
                new Mpa(1,null,null),
                new ArrayList<>());
        User user = new User(1,
                "ivanov@mail.com",
                "ivanov94",
                "ivanov",
                LocalDate.of(1994, 3, 28));
        Film film1 = filmDbStorage.create(film);
        User user1 = userDbStorage.create(user);

        filmDbStorage.addLike(film1.getId(), user1.getId());
        filmDbStorage.deleteLike(film1.getId(), user1.getId());

        Boolean bool = jdbcTemplate.queryForObject("select exists " +
                        "(select * from likes where film_id = ? and user_id = ?)",
                Boolean.class, film.getId(), user.getId());

        assertFalse(bool);
    }

    @Test
    public void testGetPopular() {
        Film film = new Film(1,
                "film",
                "description",
                LocalDate.of(2000,1,1),
                90,
                new Mpa(1,"G","У фильма нет возрастных ограничений"),
                new ArrayList<>());
        Film film2 = new Film(2,
                "film2",
                "description2",
                LocalDate.of(2000,3,5),
                124,
                new Mpa(4,null,null),
                new ArrayList<>());

        Film filmCreate1 = filmDbStorage.create(film);
        Film filmCreate2 = filmDbStorage.create(film2);

        User user = new User(1,
                "ivanov@mail.com",
                "ivanov94",
                "ivanov",
                LocalDate.of(1994, 3, 28));
        User userCreate1 = userDbStorage.create(user);
        User user2 = new User(2,
                "petrov@mail.com",
                "petrov2000",
                "petrov",
                LocalDate.of(200, 1, 15));
        User userCreate2 = userDbStorage.create(user2);

        filmDbStorage.addLike(filmCreate1.getId(), userCreate1.getId());
        filmDbStorage.addLike(filmCreate1.getId(), userCreate2.getId());

        filmDbStorage.addLike(filmCreate2.getId(), userCreate1.getId());

        assertEquals(filmDbStorage.getFilmsPopular(10), List.of(filmCreate1, filmCreate2));
    }
}