package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserServiceImpl;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
@Getter
public class UserController {
    private final UserServiceImpl userServiceImpl;

    @Autowired
    public UserController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @PostMapping
    public User add(@Valid @RequestBody User user) {
        log.info(String.format("Получен POST запрос с телом %s", user));
        return userServiceImpl.addUser(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info(String.format("Получен PUT запрос с телом %s", user));
        return userServiceImpl.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Получен PUT запрос к эндпоинту /users/id/friends/friendId");
        userServiceImpl.addFriend(id, friendId);
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Получен GET запрос к эндпоинту /users");
        return userServiceImpl.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        log.info("Получен GET запрос к эндпоинту /users/id");
        return userServiceImpl.getUserById(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        log.info("Получен GET запрос к эндпоинту /users/id/friends");
        return userServiceImpl.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Получен GET запрос к эндпоинту /users/id/friends/common/otherId");
        return userServiceImpl.getCommonFriends(id, otherId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Получен DELETE запрос к эндпоинту /users/id/friends/friendId");
        userServiceImpl.deleteFriend(id, friendId);
    }
}
