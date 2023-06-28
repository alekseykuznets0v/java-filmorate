package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User add(User user);
    User getById(int id);
    User update(User user);
    User deleteById(int id);
    List<User> getAllUsers();
}
