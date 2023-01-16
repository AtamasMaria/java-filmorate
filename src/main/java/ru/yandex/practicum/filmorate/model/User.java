package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class User {
    private int id;
    @NotNull
    @Email
    @NotBlank
    @NotEmpty
    private String email;
    @NotBlank
    @NotEmpty
    @NotNull
    private String login;
    private String name;
    @PastOrPresent
    private LocalDate birthday;
}
