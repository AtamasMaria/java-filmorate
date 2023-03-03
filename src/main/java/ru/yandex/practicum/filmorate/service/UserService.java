package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
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

    public User getUserById(Integer userId) {
        return userStorage.getUserById(userId);
    }

    public void addFriend(Integer userId, Integer friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        userStorage.addFriend(user.getId(), friend.getId());
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        userStorage.deleteFriend(user.getId(), friend.getId());
    }

    public Collection<User> getUserFriends(Integer userId) {
        User user = getUserById(userId);
        Collection<User> friends = new HashSet<>();
        for (Integer id : user.getFriendsIds()) {
            friends.add(userStorage.getUserById(id));
        }
        return friends;
    }

    public Collection<User> getCommonFriends(Integer userId, Integer otherId) {
        User user = getUserById(userId);
        User otherUser = getUserById(otherId);
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
    }
}