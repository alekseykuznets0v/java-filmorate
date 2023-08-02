package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@Slf4j
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmServiceImpl(@Qualifier("FilmDbStorage") FilmStorage filmStorage,
                           @Qualifier("UserDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public void addLike(Long userId, Long filmId) {
        userStorage.isUserIdExist(userId);
        filmStorage.addLike(userId, filmId);
    }

    @Override
    public void deleteLike(Long userId, Long filmId) {
        userStorage.isUserIdExist(userId);
        filmStorage.deleteLike(userId, filmId);
    }

    @Override
    public List<Film> getMostPopularFilms(Integer count) {
        if (count < 1) {
            String message = "Параметр запроса не может быть меньше 1";
            log.warn(message);
            throw new ValidationException(message);
        }

        return filmStorage.getMostPopularFilms(count);
    }

    @Override
    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    @Override
    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    @Override
    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id);
    }

    @Override
    public void deleteAllFilms() {
        filmStorage.deleteAllFilms();
    }

    @Override
    public void resetIdentifier() {
        filmStorage.setIdentifier(0);
    }

}
