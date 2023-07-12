package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/users")
@Slf4j
@Getter
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User add(@Valid @RequestBody User user) throws ValidationException {
        log.info(String.format("Получен POST запрос с телом %s", user));
        return userService.getUserStorage().addUser(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) throws ValidationException {
        log.info(String.format("Получен PUT запрос с телом %s", user));
        return userService.getUserStorage().updateUser(user);
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Получен GET запрос к эндпоинту /users");
        return userService.getUserStorage().getAllUsers();
    }
}
