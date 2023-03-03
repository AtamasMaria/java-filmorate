package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {

    private final FilmDbStorage filmStorage;

    @Test
    public void testGetFilmById() {

        Film createFilm = new Film(1,
                "first",
                "first description",
                LocalDate.now().minusYears(8),
                90,
                3,
                new Mpa(1,"o","o"),
                new ArrayList<>(),
                new ArrayList<>());
        filmStorage.create(createFilm);

        Film dbFilm = filmStorage.getFilmById(1);
        assertThat(dbFilm).hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    void getAllFilms() {
        Film first = new Film(1,
                "first",
                "first description",
                LocalDate.now().minusYears(8),
                90,
                3,
                new Mpa(1,"o","o"),
                new ArrayList<>(),
                new ArrayList<>());
        Film second = new Film(2,
                "second",
                "second description",
                LocalDate.now().minusYears(15),
                100,
                2,
                new Mpa(3,"o","o"),
                new ArrayList<>(),
                new ArrayList<>());
        filmStorage.create(first);
        filmStorage.create(second);

        Collection<Film> dbFilms = filmStorage.findAllFilms();
        assertEquals(2, dbFilms.size());
    }

    @Test
    void updateFilm() {
        Film first = new Film(1,
                "first",
                "first description",
                LocalDate.now().minusYears(8),
                90,
                3,
                new Mpa(1,"o","o"),
                new ArrayList<>(),
                new ArrayList<>());
        Film added = filmStorage.create(first);
        added.setName("update");
        filmStorage.update(added);
        Film dbFilm = filmStorage.getFilmById(added.getId());
        assertThat(dbFilm).hasFieldOrPropertyWithValue("name", "update");
    }

    @Test
    void deleteFilm() {
        Film first = new Film(1,
                "first",
                "first description",
                LocalDate.now().minusYears(8),
                90,
                3,
                new Mpa(1,"o","o"),
                new ArrayList<>(),
                new ArrayList<>());
        Film second = new Film(0,
                "second",
                "second description",
                LocalDate.now().minusYears(15),
                100,
                2,
                new Mpa(3,"o","o"),
                new ArrayList<>(),
                new ArrayList<>());
        Film addedFirst = filmStorage.create(first);
        Film addedSecond = filmStorage.create(second);

        Collection<Film> beforeDelete = filmStorage.findAllFilms();
        Collection<Film> afterDelete = filmStorage.findAllFilms();
        assertEquals(beforeDelete.size() - 1, afterDelete.size());
    }
}