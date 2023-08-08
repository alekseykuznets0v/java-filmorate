package ru.yandex.practicum.filmorate.storage.dao.like;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikeDaoImpl implements LikeDao {
    private final JdbcTemplate jdbcTemplate;
    @Override
    public void addLike(Long filmId, Long userId) {
        log.info("В БД отправлен запрос addLike с параметрами filmId=" + filmId + " и userId=" + userId);
        String request = "INSERT INTO likes (film_id, user_id) " +
                         "VALUES (?, ?)";
        jdbcTemplate.update(request, filmId, userId);
        log.info("Добавлен лайк пользователя с id={} к фильму с id={}", userId, filmId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        log.info("В БД отправлен запрос deleteLike с параметрами filmId=" + filmId + " и userId=" + userId);
        String request = "DELETE FROM likes " +
                         "WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(request, filmId, userId);
        log.info("Удален лайк пользователя с id={} к фильму с id={}", userId, filmId);
    }

    @Override
    public Set<Long> getLikesByFilmId(Long filmId) {
        log.info("В БД отправлен запрос getLikesByFilmId с параметром: " + filmId);
        String request = "SELECT user_id " +
                         "FROM likes " +
                         "WHERE film_id = ?";
        log.info("Получен список лайков к фильму с id={}", filmId);
        return new HashSet<>(jdbcTemplate.query(request, (rs, rowNum) -> rs.getLong("user_id"), filmId));
    }
}
