package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase
public class UserDbStorageTest {
    private final UserDbStorage userDbStorage;
    private final JdbcTemplate jdbcTemplate;

   @BeforeEach
    public void beforeEach(){
        jdbcTemplate.update("DELETE FROM users");
        jdbcTemplate.update("DELETE FROM friends");
        jdbcTemplate.update("ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1");
    }

    @Test
    public void testGetUserById() {
        User user = new User(1,
                "ivanov@mail.com",
                "ivanov94",
                "ivanov",
                LocalDate.of(1994, 3, 28));
        User user1 = userDbStorage.create(user);

        assertThat(user1).hasFieldOrPropertyWithValue("id", 1);

        assertEquals(user, user1);
    }

    @Test
    public void testGetUserWithWrongId() {
        User user = new User(
                "ivanov@mail.com",
                "ivanov94",
                "ivanov",
                LocalDate.of(1994, 3, 28));
        User user1 = userDbStorage.create(user);

        UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> {
            userDbStorage.getUserById(100);
        });

        assertEquals(e.getMessage(), "Пользован с таким id не зарегестрирован.");
    }

    @Test
    public void testFindAllUsers() {
        User user = new User(
                "ivanov@mail.com",
                "ivanov94",
                "ivanov",
                LocalDate.of(1994, 3, 28));
        User userCreate1 = userDbStorage.create(user);

        User user2 = new User(
                "petrov@mail.com",
                "petrov2000",
                "petrov",
                LocalDate.of(2000, 1, 15));
        User userCreate2 = userDbStorage.create(user2);

        Collection<User> users = userDbStorage.findAllUsers();

        assertEquals(2, users.size());
        assertEquals(userDbStorage.findAllUsers(), List.of(userCreate1, userCreate2));
    }

    @Test
    public void testCreateUserWithSomeId() {
        User user = new User(
                "ivanov@mail.com",
                "ivanov94",
                "ivanov",
                LocalDate.of(1994, 3, 28));
        User userCreate1 = userDbStorage.create(user);

        assertThat(userCreate1).hasFieldOrPropertyWithValue("id", 1);

        UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> {
            userDbStorage.getUserById(546);
        });

        assertEquals(e.getMessage(), "Пользован с таким id не зарегестрирован.");
    }

    @Test
    public void testUpdateUser() {
        User user = new User(
                "ivanov@mail.com",
                "ivanov94",
                "ivanov",
                LocalDate.of(1994, 3, 28));
        User userCreate1 = userDbStorage.create(user);

        userDbStorage.update(userCreate1);

        assertThat(userCreate1).hasFieldOrPropertyWithValue("id", 1);

        assertEquals(userCreate1, userDbStorage.getUserById(1));
    }

    @Test
    public void testAddFriend() {
        User user = new User(
                "ivanov@mail.com",
                "ivanov94",
                "ivanov",
                LocalDate.of(1994, 3, 28));
        User userCreate1 = userDbStorage.create(user);

        User user2 = new User(
                "petrov@mail.com",
                "petrov2000",
                "petrov",
                LocalDate.of(2000, 1, 15));
        User userCreate2 = userDbStorage.create(user2);

        userDbStorage.addFriend(userCreate1.getId(), userCreate2.getId());

        assertTrue(userDbStorage.getUserFriends(userCreate1.getId()).contains(userCreate2));
    }

    @Test
    public void testAddFriendWithWrongId() {
        User user = new User(
                "ivanov@mail.com",
                "ivanov94",
                "ivanov",
                LocalDate.of(1994, 3, 28));
        User userCreate1 = userDbStorage.create(user);
        UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> {
            userDbStorage.addFriend(1, 100);
        });

        assertEquals(e.getMessage(), "Пользователь не был обнаружен.");
    }

    @Test
    public void testDeleteFriend() {
        User user = new User(
                "ivanov@mail.com",
                "ivanov94",
                "ivanov",
                LocalDate.of(1994, 3, 28));
        User userCreate1 = userDbStorage.create(user);

        User user2 = new User(
                "petrov@mail.com",
                "petrov2000",
                "petrov",
                LocalDate.of(2000, 1, 15));
        User userCreate2 = userDbStorage.create(user2);

        userDbStorage.addFriend(userCreate1.getId(), userCreate2.getId());

        userDbStorage.deleteFriend(userCreate1.getId(), userCreate2.getId());

        assertFalse(userDbStorage.getUserFriends(userCreate2.getId()).contains(userCreate1));
    }

    @Test
    public void testGetCommonFriends() {
        User user = new User(
                "ivanov@mail.com",
                "ivanov94",
                "ivanov",
                LocalDate.of(1994, 3, 28));
        User userCreate1 = userDbStorage.create(user);

        User user2 = new User(
                "petrov@mail.com",
                "petrov2000",
                "petrov",
                LocalDate.of(2000, 1, 15));
        User userCreate2 = userDbStorage.create(user2);

        userDbStorage.addFriend(userCreate1.getId(), userCreate2.getId());

        User user3 = new User(
                "sokolov@mail.com",
                "sokolov2002",
                "sokolovV",
                LocalDate.of(2002, 2, 7));

        User userCreate3 = userDbStorage.create(user3);

        userDbStorage.addFriend(userCreate3.getId(), userCreate2.getId());

        assertEquals(userDbStorage.getCommonFriends(userCreate3.getId(), userCreate1.getId()), List.of(userCreate2));
    }

}
