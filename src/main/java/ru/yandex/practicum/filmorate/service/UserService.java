package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;


@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private Integer userId;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
        this.userId = 0;
    }


    public User create(User user) {
        validateUser(user);
        user.setId(++userId);
        return userStorage.create(user);
    }

    public User update(User user) {
        validateUser(user);
        return userStorage.update(user);
    }

    public List<User> findAll() {
        return userStorage.findAllUsers();
    }

    public User getUserById(Integer userId) {
        return userStorage.getUserById(userId);
    }

    public void addFriend(Integer userId, Integer friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        userStorage.addToFriend(user.getId(), friend.getId());

    }

    public void deleteFriend(Integer userId, Integer friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        userStorage.deleteFromFriend(user.getId(), friend.getId());
    }

    public List<User> getUserFriends(Integer userId) {
        return userStorage.getUserFriends(userId);
    }

    public List<User> getCommonFriends(Integer userId, Integer friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        return userStorage.getCommonFriends(user.getId(), friend.getId());
    }
    public void validateUser(User user){
        if (user.getLogin().isBlank()) {
            log.info("Логин пользователя не задан");
            throw new ValidationException("Логин пользователя не задан");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.info("Неверная дата рождения");
            throw new ValidationException("Неверная дата рождения");
        }
        if (user.getName().isEmpty())
            user.setName(user.getLogin());
    }
}