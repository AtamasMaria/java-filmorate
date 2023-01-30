package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.annotation.CorrectReleaseDay;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {
    @PositiveOrZero(message = "id can not be negative.")
    private Integer id;
    @NotBlank(message = "The title of the movie can't be empty.")
    private String name;
    @Length(min = 1, max = 200, message = "The movie description exceeds the maximum number of characters 200.")
    private String description;
    @CorrectReleaseDay(message = "The release date cannot be earlier than December 28, 1895.")
    private LocalDate releaseDate;
    @PositiveOrZero(message = "The duration of the film cannot be negative.")
    private Integer duration;

    private Set<Integer> likesByUsers;

    public void addLike(Integer id) {
        if (likesByUsers == null){
            likesByUsers = new HashSet<>();
        }
        likesByUsers.add(id);
    }

    public void deleteLike(Integer id) {
        likesByUsers.remove(id);
    }


}
