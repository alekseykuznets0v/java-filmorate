package ru.yandex.practicum.filmorate.storage.dao.friends;

import java.util.Set;

public interface FriendsDao {
    Set<Long> getFriendsIdByUserId(Long id);

    void addFriendRequest(Long userId, Long friendId);

    void deleteFriend(Long fromUserId, Long toFriendId);
}
