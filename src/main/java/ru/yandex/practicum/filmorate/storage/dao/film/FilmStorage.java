package ru.yandex.practicum.filmorate.storage.dao.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> getAllFilms();

    Optional<Film> getFilmById(Long id);

    Optional<Film> addFilm(Film film);

    Film updateFilm(Film film);

    void deleteAllFilms();

    int deleteFilmById(Long id);

    List<Film> getMostPopularFilms(Integer count);

    int addLike(Long userId, Long filmId);

    int deleteLike(Long userId, Long filmId);
}
