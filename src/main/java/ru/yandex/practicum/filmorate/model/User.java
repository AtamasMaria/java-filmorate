package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.*;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.annotation.CorrectLogin;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class User {
    @PositiveOrZero
    private Integer id;

    @Email(message = "Email entered incorrectly.")
    @NotBlank(message = "email cannot be empty.")
    private String email;
    @CorrectLogin
    @NotBlank(message = "The login must not be empty and contain spaces.")

    private String login;
    private String name;
    @PastOrPresent(message = "Birthday can't be from the future.")
    private LocalDate birthday;
    private List<Integer> friendsIds;

}
