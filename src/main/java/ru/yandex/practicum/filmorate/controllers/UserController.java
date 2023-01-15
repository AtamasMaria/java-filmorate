package ru.yandex.practicum.filmorate.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 0;

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            log.warn("Попытка повторной регестрации.");
            throw new ValidationException("This email has already been registered.");
        }
        validateUser(user);
        user.setId(++id);
        users.put(user.getId(), user);
        log.debug("Зарегестрирован пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            validateUser(user);
            users.put(user.getId(), user);
            log.debug("Изменен пользователь: {}", user);
            return user;
        } else {
            log.warn("Попытка обновить информацию о пользователе с неправильным id.");
            throw new ValidationException("No user with this id was found.");
        }
    }


    private void validateUser(User user) {
        LocalDate now = LocalDate.now();
        if (user.getEmail() == null || user.getEmail().isEmpty() || user.getEmail().isBlank()
                || !user.getEmail().contains("@")) {
            log.warn("Попытка создать пользователя с некорректным адресом email");
            throw new ValidationException("Email entered incorrectly");
        } else if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().isBlank()
                || user.getLogin().contains(" ")) {
            log.warn("Попытка создать пользователя с некорректным логином.");
            throw new ValidationException("The login must not be empty and contain spaces.");
        } else if (user.getBirthday().isAfter(now)) {
            log.warn("Попытка создать пользователя с датой рождения из будущего");
            throw new ValidationException("Birthday can't be from the future");
        }
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            log.warn("Попытка создать пользователя с пустым именем, вместо имени будет присвоен логин");
            user.setName(user.getLogin());
        }
    }
}


