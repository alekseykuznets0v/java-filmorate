package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

public interface UserService {

    void addFriend(Long id, Long friendId);

    void deleteFriend(Long id, Long friendId);

    List<User> getFriends(Long id);

    List<User> getCommonFriends(Long id, Long friendId);

    Collection<User> getAllUsers();

    User getUserById(Long id);

    User addUser(User user);

    User updateUser(User user);

    void deleteAllUsers();
}
