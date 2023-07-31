package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> getAllFilms();

    Film getFilmById(Long id);

    Film addFilm(Film film);

    Film updateFilm(Film film);

    void setIdentifier(long identifier);

    void deleteAllFilms();

    boolean isFilmIdExist(Long id);
}
