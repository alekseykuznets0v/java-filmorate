package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

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
        userStorage.addFriend(id, friendId);
    }

    @Override
    public void deleteFriend(Long id, Long friendId) {
        userStorage.deleteFriend(id, friendId);
    }

    @Override
    public List<User> getFriends(Long id) {
        List<Optional<User>> friends = userStorage.getFriends(id);
        return friends.stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
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
        Optional<User> user = userStorage.getUserById(id);

        if (user.isPresent()) {
            return user.get();
        } else {
            throw new NotFoundException(String.format("Пользователь с id=%s не найден", id));
        }
    }

    @Override
    public User addUser(User user) {
        Optional<User> optionalUser = userStorage.addUser(user);

        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new AlreadyExistsException(String.format("Пользователь с email=%s уже существует", user.getEmail()));
        }
    }

    @Override
    public User updateUser(User user) {
        Optional<User> optionalUser = userStorage.getUserById(user.getId());

        if (optionalUser.isPresent()) {
            return userStorage.updateUser(user);
        } else {
            throw new NotFoundException(String.format("Пользователь с id=%s не найден", user.getId()));
        }
    }

    @Override
    public void deleteAllUsers() {
        userStorage.deleteAllUsers();
    }
}
