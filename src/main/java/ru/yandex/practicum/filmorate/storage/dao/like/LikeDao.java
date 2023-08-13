package ru.yandex.practicum.filmorate.storage.dao.like;

import java.util.Set;

public interface LikeDao {
    int addLike(Long filmId, Long userId);

    int deleteLike(Long filmId, Long userId);

    Set<Long> getLikesByFilmId(Long filmId);

    Integer getLikesNumberByFilmId(Long filmId);
}
