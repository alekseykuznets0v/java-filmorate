package ru.yandex.practicum.filmorate.storage.dao.friends;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class FriendsDaoImpl implements FriendsDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Set<Long> getFriendsIdByUserId(Long userId) {
        log.info("В БД отправлен запрос getFriendsIdByUserId с параметром" + userId);
        String request = "SELECT friend_id " +
                         "FROM friends " +
                         "WHERE user_id = ? " +
                         "AND approved = true";

        return new HashSet<>(jdbcTemplate.query(request, (rs, rowNum) -> rs.getLong("friend_id"), userId));
    }

    @Override
    public void addFriendRequest(Long fromUserId, Long toFriendId) {
        log.info("В БД отправлен запрос addFriendRequest с параметрами userId=" + fromUserId + " и friendId=" + toFriendId);
        String request = "INSERT INTO friends (user_id, friend_id, approved) " +
                         "VALUES (?, ?, ?)";
        String updateApproval = "UPDATE friends " +
                                "SET approved = true " +
                                "WHERE user_id = ? " +
                                "AND friend_id = ?";

        if (isReverseFriendRequestExists(fromUserId, toFriendId)) {
            jdbcTemplate.update(updateApproval, toFriendId, fromUserId);
            jdbcTemplate.update(request, fromUserId, toFriendId, true);
        } else {
            jdbcTemplate.update(request, fromUserId, toFriendId, true);
            jdbcTemplate.update(request, toFriendId, fromUserId, false);
        }
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        log.info("В БД отправлен запрос deleteFriend с параметрами userId=" + userId + " и friendId=" + friendId);
        String request = "DELETE FROM friends " +
                         "WHERE user_id = ? " +
                         "AND friend_id = ?";
        jdbcTemplate.update(request, userId, friendId);
    }

    /* Метод isReverseFriendRequestExists проверяет наличие обратного запроса,
       т.е. есть ли уже запрос на дружбу от того, кого пользователь хочет добавить в друзья
    */
    private boolean isReverseFriendRequestExists(Long fromUserId, Long toFriendId) {
        String request = "SELECT * " +
                         "FROM friends " +
                         "WHERE user_id = ? " +
                         "AND friend_id = ?";
        log.info("В БД отправлен запрос isReverseFriendRequestExists с параметрами userId=" + toFriendId + " и friendId=" + fromUserId);

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(request, toFriendId, fromUserId);

        return rowSet.next();
    }
}
