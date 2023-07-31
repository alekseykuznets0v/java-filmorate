package ru.yandex.practicum.filmorate.storage.inmemory.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.AlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.inmemory.InMemoryStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryUserStorage extends InMemoryStorage<User> implements UserStorage {

    @Override
    public User addUser(User user) {
        String email = user.getEmail();

        if (isEmailExist(email)) {
            String warning = String.format("Пользователь с email=%s уже существует", email);
            log.warn(AlreadyExistsException.class + ": " + warning);
            throw new AlreadyExistsException(warning);
        }

        long id = getIdentifier();
        user.setId(id);
        storage.put(id, user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        isUserIdExist(user.getId());
        storage.put(user.getId(), user);
        return user;
    }

    @Override
    public Collection<User> getAllUsers() {
        return storage.values();
    }

    @Override
    public User getUserById(Long id) {
        isUserIdExist(id);
        return storage.get(id);
    }

    @Override
    public void setIdentifier(long identifier) {
        this.identifier = identifier;
    }

    @Override
    public boolean isUserIdExist(Long id) {
        if (storage.containsKey(id)) {
            return true;
        } else {
            String message = String.format("Пользователь с id=%s не найден", id);
            log.warn(message);
            throw new NotFoundException(message);
        }
    }

    @Override
    public List<User> getFriends(Long id) {
        Set<Long> friendIds = getUserById(id).getFriends();

        return storage.entrySet()
                .stream()
                .filter(entry -> friendIds.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAllUsers() {
        storage.clear();
    }

    private boolean isEmailExist(String email) {
        return getAllUsers()
                .stream()
                .anyMatch(user -> email.equals(user.getEmail()));
    }
}
