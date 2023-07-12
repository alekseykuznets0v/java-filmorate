package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Getter
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend (Long id1, Long id2){
        Map<Long, User> users = userStorage.getStorage();

        if (users.containsKey(id1) & users.containsKey(id2)) {
            User user = userStorage.getStorage().get(id1);
            User friend = userStorage.getStorage().get(id2);

            user.getFriends().add(id2);
            friend.getFriends().add(id1);
        } else {
            long id = users.containsKey(id1) ? id2 : id1;
            throw new NotFoundException(String.format("Пользователь с id=%s не найден", id));
        }
    }

    public void removeFriend (Long id1, Long id2){
        Map<Long, User> users = userStorage.getStorage();

        if (users.containsKey(id1) & users.containsKey(id2)) {
            User user = userStorage.getUserById(id1);
            User friend = userStorage.getUserById(id2);

            if(user.getFriends().contains(id2)) {
                user.getFriends().remove(id2);
                friend.getFriends().remove(id1);
            } else {
                throw new NotFoundException(String.format("У пользователя %s нет в списке друга с id=%s",
                        user.getName(), id2));
            }
        } else {
            long id = users.containsKey(id1) ? id2 : id1;
            throw new NotFoundException(String.format("Пользователь с id=%s не найден", id));
        }
    }

    public List<User> getFriends (Long id) {
        Map<Long, User> users = userStorage.getStorage();
        Set<Long> friendIds = userStorage.getStorage().get(id).getFriends();

        if (users.containsKey(id)) {
            return users.entrySet().stream()
                    .filter(entry -> friendIds.contains(entry.getKey()))
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException(String.format("Пользователь с id=%s не найден", id));
        }
    }
}
