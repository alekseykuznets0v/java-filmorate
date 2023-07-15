package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

public interface FilmService {

    void addLike(Long userId, Long filmId);

    void deleteLike(Long userId, Long filmId);

    List<Film> getMostPopularFilms(Integer count);

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Collection<Film> getAllFilms();

    Film getFilmById(Long id);

    void deleteAllFilms();

    void resetIdentifier();
}
