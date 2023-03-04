package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User create(User user);

    User update(User user);

    List<User> findAllUsers();

    User getUserById(Integer userId);

    List<Integer> getUserFriends(Integer userId);

    boolean addFriend(int userId, int friendId);
    boolean deleteFriend(int userId, int friendId);
}