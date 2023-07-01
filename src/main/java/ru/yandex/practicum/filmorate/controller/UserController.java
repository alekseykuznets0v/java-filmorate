package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
@Getter
public class UserController extends Controller<User> {
    private final Map<String, Integer> emails = new HashMap<>();

    @PostMapping
    public User add(@Valid @RequestBody User user) throws ValidationException {
        log.info(String.format("Получен POST запрос с телом %s", user));
        String email = user.getEmail();

        if (emails.containsKey(email)) {
            String warning = String.format("Пользователь с email=%s уже существует", email);
            log.warn(ValidationException.class + ": " + warning);
            throw new ValidationException(warning);
        }

        int id = getIdentifier();
        user.setId(id);
        storage.put(id, user);
        emails.put(user.getEmail(), id);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) throws ValidationException {
        log.info(String.format("Получен PUT запрос с телом %s", user));

        if (storage.containsKey(user.getId())) {
            storage.put(user.getId(), user);
            return user;
        } else {
            String warning = String.format("В базе данных нет пользователя с id=%s", user.getId());
            log.warn(ValidationException.class + ": " + warning);
            throw new ValidationException(warning);
        }
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Получен GET запрос к эндпоинту /users");
        return getAllValues();
    }
}
