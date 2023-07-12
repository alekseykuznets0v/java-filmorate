package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Entity;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;

public interface FilmStorage {
    Collection<Film> getAllFilms();

    Map<Long, Film> getFilmStorage();

    Film getFilmById(Long id);

    Film addFilm(Film film);

    Film updateFilm(Film film);

}
