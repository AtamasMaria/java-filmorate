package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.annotation.CorrectReleaseDay;
import java.time.LocalDate;
import java.util.*;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    @PositiveOrZero(message = "id can not be negative")
    private int id;
    @NotBlank(message = "name must not be empty")
    private String name;
    @Length(min = 1, max = 200, message = "description length must be between 1 and 200")
    private String description;
    @CorrectReleaseDay(message = "releaseDate must be after 28-DEC-1895")
    private LocalDate releaseDate;
    @PositiveOrZero(message = "duration can not be negative")
    private Integer duration;
    private int rate;
    @NotNull
    private Mpa mpa;
    private List<Genre> genres = new ArrayList<>();

    private List<Integer> likes = new ArrayList<>();


}
