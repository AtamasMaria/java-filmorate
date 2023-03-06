package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GenreDbStorageTest {

    private final GenreDbStorage genreDbStorage;

    @Test
    public void testGetGenreById() {
        Genre genre = genreDbStorage.getGenreById(1);

        assertThat(genre).hasFieldOrPropertyWithValue("id", 1);
        assertThat(genre).hasFieldOrPropertyWithValue("name", "Комедия");
    }

    @Test
    public void testGetGenreByIdWithWrongId() {
        NotFoundException e = assertThrows(NotFoundException.class, () -> {
            genreDbStorage.getGenreById(10);
        });

        assertEquals(e.getMessage(), "Жанр не найден.");
    }

    @Test
    public void testGetAllGenres() {
        assertEquals(genreDbStorage.getAllGenres().size(), 6);
    }

}
