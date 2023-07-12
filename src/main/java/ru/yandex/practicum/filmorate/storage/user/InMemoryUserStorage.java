package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.AlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage extends InMemoryStorage<User> implements UserStorage {
    private final Map<String, Long> emails;

    public InMemoryUserStorage() {
        super();
        this.emails = new HashMap<>();
    }

    @Override
    public User addUser(User user) {
        String email = user.getEmail();

        if (emails.containsKey(email)) {
            String warning = String.format("Пользователь с email=%s уже существует", email);
            log.warn(AlreadyExistsException.class + ": " + warning);
            throw new AlreadyExistsException(warning);
        }

        long id = getIdentifier();
        user.setId(id);
        emails.put(email, id);
        return storage.put(id, user);
    }

    @Override
    public User updateUser(User user) {
        if (storage.containsKey(user.getId())) {
            return storage.put(user.getId(), user);
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
    public Map<Long, User> getUserStorage() {
        return storage;
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
    public Map<String, Long> getEmails() {
        return emails;
    }
}
