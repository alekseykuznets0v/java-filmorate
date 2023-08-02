package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    Collection<Film> getAllFilms();

    Film getFilmById(Long id);

    Film addFilm(Film film);

    Film updateFilm(Film film);

    void deleteAllFilms();

    void deleteFilmById(Long id);

    void setIdentifier(long identifier);

    void isFilmIdExist(Long id);

    List<Film> getMostPopularFilms(Integer count);

    void addLike(Long userId, Long filmId);

    void deleteLike(Long userId, Long filmId);
}
