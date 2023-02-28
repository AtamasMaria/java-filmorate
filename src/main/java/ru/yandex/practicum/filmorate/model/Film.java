package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

import lombok.Data;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.annotation.CorrectReleaseDay;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Data
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
    @NonNull
    private Mpa mpa;
    private Set<Integer> likes;
    private List<Genre> genres;

    public Film(int id, String name, String description, LocalDate releaseDate, int duration, Mpa mpa, List<Genre> genres) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.genres = genres;
    }

}
