package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.CorrectLogin;

import java.time.LocalDate;

@Data
@Builder
public class User {
    @PositiveOrZero
    private Integer id;
    @Email (message = "Email entered incorrectly.")
    @NotBlank (message = "email cannot be empty.")
    private String email;
    @CorrectLogin
    @NotBlank (message = "The login must not be empty and contain spaces.")
    private String login;
    private String name;
    @PastOrPresent(message = "Birthday can't be from the future.")
    private LocalDate birthday;
}
