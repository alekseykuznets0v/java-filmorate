package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
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
        Set<Film> filmChart = filmStorage.getFilmsChart();

        if (isUserIdExist(userId) && isFilmIdExist(filmId)) {
            Film film = films.get(filmId);
            filmChart.remove(film);
            film.getLikes().add(userId);
            filmChart.add(film);
        }
    }

    public void deleteLike(Long userId, Long filmId) {
        Map<Long, Film> films = filmStorage.getStorage();
        Set<Film> filmChart = filmStorage.getFilmsChart();

        if (isUserIdExist(userId) && isFilmIdExist(filmId)) {
            Film film = films.get(filmId);
            filmChart.remove(film);
            film.getLikes().remove(userId);
            filmChart.add(film);
        }
    }

    public List<Film> getMostPopularFilms (Integer count) {
        if (count < 1) throw new ValidationException("Параметр запроса не может быть меньше 1");

        TreeSet<Film> filmsChart = new TreeSet<>(filmStorage.getFilmsChart());
        List<Film> mostPopularFilms = new ArrayList<>();

        while(mostPopularFilms.size() < count && !filmsChart.isEmpty()) {
            mostPopularFilms.add(filmsChart.pollLast());
        }

        return mostPopularFilms;
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
