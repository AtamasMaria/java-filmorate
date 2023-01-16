package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.Duration;
import java.time.LocalDate;

@Data
@Builder
public class Film {
    private int id;
    @NotBlank
    @NotEmpty
    @NonNull
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;

}
