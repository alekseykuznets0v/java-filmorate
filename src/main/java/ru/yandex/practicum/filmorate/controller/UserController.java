package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.HashMap;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private int identifier = 0;
    private final HashMap<Integer, User> users = new HashMap<>();

    @PostMapping
    public User addUser(@Valid @RequestBody User user) throws ValidationException {
        if (users.containsKey(user.getId()) || users.containsValue(user)){
            log.warn(ValidationException.class + ": Пользователь уже существует");
            throw new ValidationException(String.format("Пользователь с id=%s уже существует", user.getId()));
        }

        if (user.getName() == null) {
            log.info("Получен запрос на добавление пользователя с пустым полем 'name'");
            user.setName(user.getLogin());
        }

        user.setId(getIdentifier());
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) throws ValidationException {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return user;
        } else {
            log.warn(ValidationException.class + ": В базе данных нет такого пользователя");
            throw new ValidationException(String.format("В базе данных нет пользователя с id=%s", user.getId()));
        }
    }

    @GetMapping
    public Collection<User> getAllUsers(){
        return users.values();
    }

    private int getIdentifier(){
        return ++identifier;
    }

    public void setIdentifier(int identifier){
        this.identifier = identifier;
    }

    public HashMap<Integer, User> getUsers() {
        return users;
    }
}
