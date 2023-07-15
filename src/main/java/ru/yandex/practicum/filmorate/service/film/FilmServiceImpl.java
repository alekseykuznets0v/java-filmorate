package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmServiceImpl(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public void addLike(Long userId, Long filmId) {
        if (isUserIdExist(userId) && isFilmIdExist(filmId)) {
            Film film = getFilmById(filmId);
            film.getLikes().add(userId);
            film.addLike();
        }
    }

    @Override
    public void deleteLike(Long userId, Long filmId) {
        if (isUserIdExist(userId) && isFilmIdExist(filmId)) {
            Film film = getFilmById(filmId);
            film.getLikes().remove(userId);
            film.removeLike();
        }
    }

    @Override
    public List<Film> getMostPopularFilms(Integer count) {
        if (count < 1) {
            String message = "Параметр запроса не может быть меньше 1";
            log.warn(message);
            throw new ValidationException(message);
        }

        Set<Film> filmsChart = new TreeSet<>(Comparator.comparing(Film::getLikesNumber).reversed()
                .thenComparing(Film::getId));
        filmsChart.addAll(filmStorage.getAllFilms());

        return filmsChart.stream().limit(count).collect(Collectors.toList());
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

    private boolean isUserIdExist(Long id) {
        return userStorage.isUserIdExist(id);
    }

    private boolean isFilmIdExist(Long id) {
        return filmStorage.isFilmIdExist(id);
    }
}
