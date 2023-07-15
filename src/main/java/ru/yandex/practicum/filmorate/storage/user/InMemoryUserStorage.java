package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.AlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;

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
        if (storage.containsKey(user.getId())) {
            storage.put(user.getId(), user);
            return storage.get(user.getId());
        } else {
            String warning = String.format("В базе данных отсутствует пользователь с id=%s", user.getId());
            log.warn(NotFoundException.class + ": " + warning);
            throw new NotFoundException(warning);
        }
    }

    @Override
    public Collection<User> getAllUsers() {
        return storage.values();
    }

    @Override
    public User getUserById(Long id) {
        if (storage.containsKey(id)) {
            return storage.get(id);
        } else {
            String warning = String.format("В базе данных отсутствует пользователь с id=%s", id);
            log.warn(NotFoundException.class + ": " + warning);
            throw new NotFoundException(warning);
        }
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
        List<User> friends = new ArrayList<>();

        if (isUserIdExist(id)) {
            Set<Long> friendIds = getUserById(id).getFriends();

            friends.addAll(storage.entrySet()
                    .stream()
                    .filter(entry -> friendIds.contains(entry.getKey()))
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList()));
        }

        return friends;
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
