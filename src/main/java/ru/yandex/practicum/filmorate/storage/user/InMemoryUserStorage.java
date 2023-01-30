package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private static int id = 0;

    @Override
    public User create(User user) {
        if (users.containsKey(user)) {
            throw new ValidationException("");
        }
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            log.warn("Попытка создать пользователя с пустым именем, вместо имени будет присвоен логин");
            user.setName(user.getLogin());
        }
        user.setId(++id);
        users.put(user.getId(), user);
        log.debug("Зарегестрирован пользователь: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.debug("Изменен пользователь: {}", user);
            return user;
        } else {
            log.warn("Попытка обновить информацию о пользователе с неправильным id.");
            throw new ValidationException("No user with this id was found.");
        }
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User getUserById(Integer userId) {
        if (!users.containsKey(userId)) {
            log.warn("Попытка получить пользователя с не существующим id");
            throw new UserNotFoundException(String.format("Пользователь с id=%d не найден", id));
        }
        return users.get(userId);
    }

    @Override
    public void delete(User user) {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException(String.format("Пользователь с id=%d не найден", id));
        }
        users.remove(user.getId());
    }
}