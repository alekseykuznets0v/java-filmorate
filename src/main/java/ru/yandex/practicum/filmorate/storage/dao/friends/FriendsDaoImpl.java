package ru.yandex.practicum.filmorate.storage.dao.friends;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class FriendsDaoImpl implements FriendsDao {
    private final JdbcTemplate jdbcTemplate;
    private static final String WHERE_USER_ID = "WHERE user_id = ? ";
    private static final String AND_FRIEND_ID = "AND friend_id = ? ";

    @Override
    public Set<Long> getFriendsIdByUserId(Long userId) {
        String request = "SELECT friend_id " +
                         "FROM friends " +
                         WHERE_USER_ID +
                         "AND approved = true";

        return new HashSet<>(jdbcTemplate.query(request, (rs, rowNum) -> rs.getLong("friend_id"), userId));
    }

    @Override
    public void addFriendRequest(Long fromUserId, Long toFriendId) {
        String request = "INSERT INTO friends (user_id, friend_id, approved) " +
                         "VALUES (?, ?, ?)";
        String updateApproval = "UPDATE friends " +
                                "SET approved = true " +
                                WHERE_USER_ID +
                                AND_FRIEND_ID;

        if (isReverseFriendRequestExists(fromUserId, toFriendId)) {
            jdbcTemplate.update(updateApproval, toFriendId, fromUserId);
        }

        jdbcTemplate.update(request, fromUserId, toFriendId, true);
        jdbcTemplate.update(request, toFriendId, fromUserId, false);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        String request = "DELETE FROM friends " +
                         WHERE_USER_ID +
                         AND_FRIEND_ID;
        jdbcTemplate.update(request, userId, friendId);
    }

    /* Метод isReverseFriendRequestExists проверяет наличие обратного запроса,
       т.е. есть ли уже запрос на дружбу от того, кого пользователь хочет добавить в друзья
    */
    private boolean isReverseFriendRequestExists(Long fromUserId, Long toFriendId) {
        String request = "SELECT * " +
                         "FROM friends " +
                         WHERE_USER_ID +
                         AND_FRIEND_ID;

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(request, toFriendId, fromUserId);

        return rowSet.next();
    }
}
