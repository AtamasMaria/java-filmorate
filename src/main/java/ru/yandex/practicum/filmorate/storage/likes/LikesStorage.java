package ru.yandex.practicum.filmorate.storage.likes;

import ru.yandex.practicum.filmorate.model.Film;

public interface LikesStorage {

    Film addLike(int filmId, int userId);


    Film deleteLike(int filmId, int userId);
}
