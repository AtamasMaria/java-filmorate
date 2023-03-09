package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase
public class MpaDbStorageTest {
    private final MpaDbStorage mpaDbStorage;

    @Test
    public void testGetMpaById() {
        Mpa mpa = mpaDbStorage.getMpaById(1);

        assertThat(mpa).hasFieldOrPropertyWithValue("id", 1);
        assertThat(mpa).hasFieldOrPropertyWithValue("name", "G");
    }

    @Test
    public void testGetMpaByIdWithWrongId() {
        NotFoundException e = assertThrows(NotFoundException.class, () -> {
            mpaDbStorage.getMpaById(10);
        });

        assertEquals(e.getMessage(), "Такого рейтинга нет");
    }

    @Test
    public void testGetAllMpa() {
        assertEquals(mpaDbStorage.getAllMpa().size(), 5);
    }
}
