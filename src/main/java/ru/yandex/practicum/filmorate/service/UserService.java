package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import lombok.AllArgsConstructor;

import java.util.stream.Collectors;
import java.util.List;
import java.util.Set;


@Service
@Slf4j
@AllArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
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
        user.addFriend(friendId);
        friend.addFriend(userId);
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.getFriendsIds().remove(friendId);
        friend.getFriendsId().remove(userId);
    }

    public List<User> getUserFriends(Integer userId) {
        return userStorage.getUserFriends(userId);
    }

    public Set<User> getCommonFriends(Integer userId, Integer friendId) {
        return getUserById(userId)
                .getFriendsId()
                .stream()
                .filter(getUserById(friendId).getFriendsId()::contains)
                .map(this::getUserById)
                .collect(Collectors.toSet());
    }
}