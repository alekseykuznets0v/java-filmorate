package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public void addFriend(Long id, Long friendId) {
        User user = getUserById(id);
        User friend = getUserById(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(id);
    }

    @Override
    public void deleteFriend(Long id, Long friendId) {
        User user = getUserById(id);
        User friend = getUserById(friendId);

        if (user.getFriends().contains(friendId)) {
            user.getFriends().remove(friendId);
            friend.getFriends().remove(id);
        } else {
            String message = String.format("У пользователя %s нет в списке друга с id=%s", user.getName(), friendId);
            log.warn(message);
            throw new NotFoundException(message);
        }
    }

    @Override
    public List<User> getFriends(Long id) {
        return userStorage.getFriends(id);
    }

    @Override
    public List<User> getCommonFriends(Long id, Long friendId) {
        List<User> commonFriends = new ArrayList<>(getFriends(id));
        final List<User> friendFriends = new ArrayList<>(getFriends(friendId));

        commonFriends.retainAll(friendFriends);
        return commonFriends;
    }

    @Override
    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    @Override
    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    @Override
    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    @Override
    public void deleteAllUsers() {
        userStorage.deleteAllUsers();
    }

    @Override
    public void resetIdentifier() {
        userStorage.setIdentifier(0);
    }
}
