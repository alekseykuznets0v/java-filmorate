package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.apachecommons.CommonsLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage extends InMemoryStorage<User> {
    private final Map<String, Integer> emails = new HashMap<>();

    @Override
    public User add(User user) {
        String email = user.getEmail();

        if (emails.containsKey(email)) {
            String warning = String.format("Пользователь с email=%s уже существует", email);
            log.warn(ValidationException.class + ": " + warning);
            throw new ValidationException(warning);
        }

        User newUser = super.add(user);
        emails.put(newUser.getEmail(), newUser.getId());
        return newUser;
    }
}
