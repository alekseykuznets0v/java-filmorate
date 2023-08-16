package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.film.FilmStorage;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;

    @Override
    public void addLike(Long userId, Long filmId) {
        int result = filmStorage.addLike(userId, filmId);

        if (result == 0) {
            throw new NotFoundException("Фильм или пользователь с таким id не найден");
        }

        log.info("Добавлен лайк пользователя с id={} к фильму с id={}", userId, filmId);
    }

    @Override
    public void deleteLike(Long userId, Long filmId) {
        int result = filmStorage.deleteLike(userId, filmId);

        if (result == 0) {
            throw new NotFoundException("Фильм или пользователь с таким id не найден");
        }

        log.info("Удален лайк пользователя с id={} к фильму с id={}", userId, filmId);
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
        Optional<Film> optionalFilm = filmStorage.addFilm(film);

        if (optionalFilm.isPresent()) {
            return optionalFilm.get();
        } else {
            throw new AlreadyExistsException("Такой фильм уже существует в БД");
        }
    }

    @Override
    public Film updateFilm(Film film) {
        Optional<Film> optionalFilm = filmStorage.getFilmById(film.getId());

        if (optionalFilm.isPresent()) {
            return filmStorage.updateFilm(film);
        } else {
            throw new NotFoundException(String.format("Фильм с id=%s не найден", film.getId()));
        }
    }

    @Override
    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    @Override
    public Film getFilmById(Long id) {
        Optional<Film> film = filmStorage.getFilmById(id);

        if (film.isPresent()) {
            return film.get();
        } else {
            throw new NotFoundException(String.format("Фильм с id=%s не найден", id));
        }
    }

    @Override
    public void deleteAllFilms() {
        filmStorage.deleteAllFilms();
    }
}
