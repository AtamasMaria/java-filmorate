package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilmControllerTest {
    Film film;
    FilmController filmController;

    @BeforeEach
    void beforeEach() {
        filmController = new FilmController();
        film = Film.builder()
                .name("������������")
                .description("��������� ����� ������ �������� ������� ��������� �������")
                .releaseDate(LocalDate.of(2020, 02, 13))
                .duration(Duration.ofMinutes(113))
                .build();
    }

    @Test
    void shouldCreateCorrectFilm() {

        filmController.create(film);
        assertEquals(film.getId(), 1);
        assertEquals(film.getName(), "������������");
    }

    @Test
    void shouldNotPassValidationFilmWithIncorrectName() {
        film.setName("");
        ValidationException exp = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertNotNull(exp.getMessage());
        assertEquals(exp.getMessage(), "The title of the movie can't be empty.");
    }

    @Test
    void shouldNotPassValidationFilmWithIncorrectDescription() {
        film.setDescription("���� ����� ���������� ��� �� ������������ ��� ������������� �����������, " +
                "� ������ �������� ����� ������������ ���������� � �������������� �������� ���������� " +
                "���������� ������������ � ����� ������� �� ���� ����������. ������ ����������� ��������� " +
                "�������� � ���, ������ ���� ����������, � ���������� ���� ������ ������������, � ������� " +
                "�������� ������� ������������ ��� ����� ��� ������� ������ �������������� ����������� " +
                "������������� ���� � �������-�����, ��������� ��������, ���������� ����������� � ���� " +
                "�������� ��������.");
        ValidationException exp = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertNotNull(exp.getMessage());
        assertEquals(exp.getMessage(), "The movie description exceeds the maximum number of characters 200");
    }

    @Test
    void shouldNotPassValidationFilmWithIncorrectReleaseDate() {
        film.setReleaseDate(LocalDate.of(1623, 3, 3));
        ValidationException exp = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertNotNull(exp.getMessage());
        assertEquals(exp.getMessage(), "The release date cannot be earlier than 12/28/1985.");
    }

    @Test
    void shouldNotPassValidationFilmWithIncorrectDuration() {
        film.setDuration(Duration.ofMinutes(-123));
        ValidationException exp = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertNotNull(exp.getMessage());
        assertEquals(exp.getMessage(), "The duration of the film cannot be negative.");
    }
}
