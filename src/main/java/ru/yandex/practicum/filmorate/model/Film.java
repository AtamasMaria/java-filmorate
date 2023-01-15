package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;

import java.time.Duration;
import java.time.LocalDate;

@Data
@Builder
public class Film {
    @Positive
    @NonNull
    private int id;
    @NotBlank
    private String name;
    @Length(min = 1, max = 200)
    private String description;
    private LocalDate releaseDate;
    @Positive
    private Duration duration;

}
