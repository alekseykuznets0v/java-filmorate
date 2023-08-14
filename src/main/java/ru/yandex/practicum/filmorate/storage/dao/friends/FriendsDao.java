package ru.yandex.practicum.filmorate.storage.dao.friends;

import java.util.Map;
import java.util.Set;

public interface FriendsDao {
    Set<Long> getFriendsIdByUserId(Long id);

    Map<Long, Set<Long>> getFriendsIdForAllUsers();

    void addFriendRequest(Long userId, Long friendId);

    void deleteFriend(Long fromUserId, Long toFriendId);
}
