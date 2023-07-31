package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {
    Collection<User> getAllUsers();

    User getUserById(Long id);

    User addUser(User user);

    User updateUser(User user);

    void setIdentifier(long identifier);

    boolean isUserIdExist(Long id);

    List<User> getFriends(Long id);

    void deleteAllUsers();
}
