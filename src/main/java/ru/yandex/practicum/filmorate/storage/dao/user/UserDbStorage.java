package ru.yandex.practicum.filmorate.storage.dao.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;

@Component("UserDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<User> getAllUsers() {
        return null;
    }

    @Override
    public User getUserById(Long id) {
        return null;
    }

    @Override
    public User addUser(User user) {
        return null;
    }

    @Override
    public User updateUser(User user) {
        return null;
    }

    @Override
    public void setIdentifier(long identifier) {

    }

    @Override
    public boolean isUserIdExist(Long id) {
        return false;
    }

    @Override
    public List<User> getFriends(Long id) {
        return null;
    }

    @Override
    public void deleteAllUsers() {

    }
}
