package ru.yandex.practicum.filmorate.storage.dao.like;

import java.util.Set;

public interface LikeDao {
    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    Set<Long> getLikesByFilmId(Long filmId);
}
