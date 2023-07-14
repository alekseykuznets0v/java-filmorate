package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Getter
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Long id, Long friendId) {
        if (isUserIdExist(id) && isUserIdExist(friendId)) {
            User user = userStorage.getStorage().get(id);
            User friend = userStorage.getStorage().get(friendId);

            user.getFriends().add(friendId);
            friend.getFriends().add(id);
        }
    }

    public void deleteFriend(Long id, Long friendId) {
        if (isUserIdExist(id) && isUserIdExist(friendId)) {
            User user = userStorage.getUserById(id);
            User friend = userStorage.getUserById(friendId);

            if(user.getFriends().contains(friendId)) {
                user.getFriends().remove(friendId);
                friend.getFriends().remove(id);
            } else {
                String message = String.format("У пользователя %s нет в списке друга с id=%s", user.getName(), friendId);
                log.warn(message);
                throw new NotFoundException(message);
            }
        }
    }

    public List<User> getFriends(Long id) {
        Map<Long, User> users = userStorage.getStorage();
        List<User> friends = new ArrayList<>();

        if (isUserIdExist(id)) {
            Set<Long> friendIds = users.get(id).getFriends();

            friends.addAll(users.entrySet().stream()
                    .filter(entry -> friendIds.contains(entry.getKey()))
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList()));
        }

        return friends;
    }

    public List<User> getCommonFriends(Long id, Long friendId) {
        Set <User> commonFriends = new HashSet<>();

        if (isUserIdExist(id) && isUserIdExist(friendId)) {
            final Set<User> friendFriends = new HashSet<>(getFriends(friendId));
            commonFriends.addAll(getFriends(id));
            commonFriends.retainAll(friendFriends);
        }

        return new ArrayList<>(commonFriends);
    }

    private boolean isUserIdExist(Long id) {
        Map<Long, User> users = userStorage.getStorage();

        if (users.containsKey(id)) {
            return true;
        } else {
            String message = String.format("Пользователь с id=%s не найден", id);
            log.warn(message);
            throw new NotFoundException(message);
        }
    }
}
