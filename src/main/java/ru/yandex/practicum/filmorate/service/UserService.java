package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
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
        if (isUserIdExist(id1) && isUserIdExist(id2)) {
            User user = userStorage.getStorage().get(id1);
            User friend = userStorage.getStorage().get(id2);

            user.getFriends().add(id2);
            friend.getFriends().add(id1);
        }
    }

    public void deleteFriend (Long id1, Long id2){
        if (isUserIdExist(id1) && isUserIdExist(id2)) {
            User user = userStorage.getUserById(id1);
            User friend = userStorage.getUserById(id2);

            if(user.getFriends().contains(id2)) {
                user.getFriends().remove(id2);
                friend.getFriends().remove(id1);
            } else {
                throw new NotFoundException(String.format("У пользователя %s нет в списке друга с id=%s",
                        user.getName(), id2));
            }
        }
    }

    public List<User> getFriends (Long id) {
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

    public List<User> getCommonFriends (Long id1, Long id2) {
        Set <User> commonFriends = new HashSet<>();

        if (isUserIdExist(id1) && isUserIdExist(id2)) {
            final Set<User> friendFriends = new HashSet<>(getFriends(id2));
            commonFriends.addAll(getFriends(id1));
            commonFriends.retainAll(friendFriends);
        }

        return new ArrayList<>(commonFriends);
    }

    private boolean isUserIdExist (Long id) {
        Map<Long, User> users = userStorage.getStorage();

        if (users.containsKey(id)) {
            return true;
        } else {
            throw new NotFoundException(String.format("Пользователь с id=%s не найден", id));
        }
    }
}
