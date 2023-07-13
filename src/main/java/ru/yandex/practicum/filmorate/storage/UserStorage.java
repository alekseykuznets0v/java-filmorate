package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;

public interface UserStorage {
    Collection<User> getAllUsers();

    Map<Long, User> getStorage();

    User getUserById(Long id);

    User addUser(User user);

    User updateUser(User user);

    Map<String, Long> getEmails();

    void setIdentifier(long identifier);
}