package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {
    User create(User user);

    User update(User user);

    List<User> findAllUsers();

    User getUserById(Integer userId);

    Collection<User> getUserFriends(Integer userId);

    void addFriend(Integer userId, Integer friendId);
    void deleteFriend(Integer userId, Integer friendId);

    Collection<User> getCommonFriends(Integer userId, Integer otherId);
}
