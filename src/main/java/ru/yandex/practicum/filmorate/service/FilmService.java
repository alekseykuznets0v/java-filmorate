package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@Getter
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(Long userId, Long filmId) {
        Map<Long, Film> films = filmStorage.getStorage();

        if (isUserIdExist(userId) && isFilmIdExist(filmId)) {
            films.get(filmId).getLikes().add(userId);
        }
    }

    public void deleteLike(Long userId, Long filmId) {
        Map<Long, Film> films = filmStorage.getStorage();

        if (isUserIdExist(userId) && isFilmIdExist(filmId)) {
            films.get(filmId).getLikes().remove(userId);
        }
    }

    public List<Film> get10MostPopularFilms() {
        TreeSet<Film> filmsChart = new TreeSet<>(filmStorage.getFilmsChart());
        List<Film> tenMostPopularFilms = new ArrayList<>();

        while(tenMostPopularFilms.size() < 10 && !filmsChart.isEmpty()) {
            tenMostPopularFilms.add(filmsChart.pollLast());
        }

        return tenMostPopularFilms;
    }

    private boolean isUserIdExist (Long id) {
        Map<Long, User> users = userStorage.getStorage();

        if (users.containsKey(id)) {
            return true;
        } else {
            throw new NotFoundException(String.format("Пользователь с id=%s не найден", id));
        }
    }

    private boolean isFilmIdExist (Long id) {
        Map<Long, Film> films = filmStorage.getStorage();

        if (films.containsKey(id)) {
            return true;
        } else {
            throw new NotFoundException(String.format("Фильм с id=%s не найден", id));
        }
    }
}
