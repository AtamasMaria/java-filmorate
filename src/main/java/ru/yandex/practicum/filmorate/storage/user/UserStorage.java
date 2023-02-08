package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User create(User user);

    User update(User user);

    List<User> findAllUsers();

    User getUserById(Integer userId);

    List<User> getUserFriends(Integer userId);
}
