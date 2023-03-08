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
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"id"})
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
    @NotNull
    private Mpa mpa;
    private List<Genre> genres = new ArrayList<>();
    private List<Integer> likes = new ArrayList<>();

    public Film(int id, String name, String description, LocalDate releaseDate,
                Integer duration, Mpa mpa, List<Integer> likes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.likes = likes;
    }

    public Film( String name, String description, LocalDate releaseDate, Integer duration, Mpa mpa, List<Genre> genres) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }



}
