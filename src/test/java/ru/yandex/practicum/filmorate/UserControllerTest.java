package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
    User user;
    UserController userController;

    @BeforeEach
    void beforeEach() {
        userController = new UserController();
        user = User.builder()
                .email("ivanov@yandex.ru")
                .name("ivanov")
                .login("ivanov-i")
                .birthday(LocalDate.of(1996, 07, 30))
                .build();
    }

    @Test
    void shouldCreateUserWithCorrectFields() {
        userController.create(user);
        assertEquals(user.getId(), 1);
        assertEquals(user.getEmail(), "ivanov@yandex.ru");
    }

    @Test
    void shouldCreateUserWithExistingId() {
        userController.create(user);
        User user2 = user;
        assertThrows(ValidationException.class, () -> userController.create(user2));
    }

    @Test
    void shouldNotPassValidationUserWithIncorrectEmail() {
        user.setEmail("email");
        ValidationException exp = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals(exp.getMessage(), "Email entered incorrectly");
    }

    @Test
    void shouldNotPassValidationUserWithIncorrectLogin() {
        user.setLogin("Ivanov i");
        ValidationException exp = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals(exp.getMessage(), "The login must not be empty and contain spaces.");
    }

    @Test
    void shouldGiveNameAsLogin() {
        user.setName("");
        userController.create(user);
        assertEquals(user.getLogin(), user.getName());
    }

    @Test
    void shouldNotPassValidationUserWithIncorrectBirthday() {
        user.setBirthday(LocalDate.of(2023, 12, 3));
        ValidationException exp = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals(exp.getMessage(), "Birthday can't be from the future");
    }

    @Test
    void shouldNotChangeUserWithWrongId() {
        user.setId(32);
        ValidationException exp = assertThrows(ValidationException.class, () -> userController.update(user));
        assertEquals(exp.getMessage(), "No user with this id was found.");
    }
}
