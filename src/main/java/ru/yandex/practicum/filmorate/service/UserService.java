package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private Integer id = 0;

    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        validateUser(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        validateUser(user);
        return userStorage.update(user);
    }

    public List<User> findAll() {
        return userStorage.findAllUsers();
    }

    public User getUserById(String userId) {
        return getStoredUser(userId);
    }

    public void addFriend(String userId, String friendId) {
        User user = getStoredUser(userId);
        User friend = getStoredUser(friendId);
        userStorage.addFriend(user.getId(), friend.getId());
    }

    public void deleteFriend(String userId, String friendId) {
        User user = getStoredUser(userId);
        User friend = getStoredUser(friendId);
        userStorage.deleteFriend(user.getId(), friend.getId());
    }

    public Collection<User> getUserFriends(String userId) {
        User user = getStoredUser(userId);
        Collection<User> friends = new HashSet<>();
        for (Integer id : user.getFriendsIds()) {
            friends.add(userStorage.getUserById(id));
        }
        return friends;
    }

    public Collection<User> getCommonFriends(String userId, String otherId) {
        User user = getStoredUser(userId);
        User otherUser = getStoredUser(otherId);
        Collection<User> commonFriends = new HashSet<>();
        for (Integer id : user.getFriendsIds()) {
            if (otherUser.getFriendsIds().contains(id)) {
                commonFriends.add(userStorage.getUserById(id));
            }
        }
        return commonFriends;
    }

    public void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty() || user.getEmail().isBlank()
                || !user.getEmail().contains("@")) {
            log.warn("Попытка создать пользователя с пустым или не корректным адресом email");
            throw new ValidationException("Адрес почты введён неверно");
        } else if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().isBlank()
                || user.getLogin().contains(" ")) {
            log.warn("Попытка создать пользователя с пустым или содержащим пробелы логином");
            throw new ValidationException("Логин не должен быть пустым и содержать пробелы");
        } else if (user.getBirthday().isBefore(LocalDate.now())) {
            log.warn("Попытка создать пользователя с датой рождения из будущего");
            throw new ValidationException("День рождения не может быть из будущего :)");
        }
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            log.warn("Попытка создать пользователя с пустым именем, вместо имени будет присвоен логин");
            user.setName(user.getLogin());
        }
        if (user.getId() == 0) {
            user.setId(++id);
        }
    }

    private Integer idFromString(final String supposedId) {
        try {
            return Integer.valueOf(supposedId);
        } catch (NumberFormatException exception) {
            return Integer.MIN_VALUE;
        }
    }

    private User getStoredUser(final String supposedId) {
        final int userId = idFromString(supposedId);
        if (userId == Integer.MIN_VALUE) {
            throw new NotFoundException("Id", "Не найден пользователь с таким id.");
        }
        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Id", "Пользователь с таким id не зарегестрирован.");
        }
        return user;
    }
}