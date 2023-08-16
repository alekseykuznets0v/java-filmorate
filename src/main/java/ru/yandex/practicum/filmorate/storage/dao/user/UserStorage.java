package ru.yandex.practicum.filmorate.storage.dao.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {
    Collection<User> getAllUsers();

    Optional<User> getUserById(Long id);

    Optional<User> addUser(User user);

    User updateUser(User user);

    void isUserIdExist(Long id);

    List<Optional<User>> getFriends(Long id);

    void deleteAllUsers();

    void deleteUserById(Long id);

    void addFriend(Long id, Long friendId);

    void deleteFriend(Long id, Long friendId);
}
