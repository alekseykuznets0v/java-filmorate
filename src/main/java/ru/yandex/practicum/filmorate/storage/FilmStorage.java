package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface FilmStorage {
    Collection<Film> getAllFilms();

    Map<Long, Film> getStorage();

    Film getFilmById(Long id);

    Film addFilm(Film film);

    Film updateFilm(Film film);

    void setIdentifier(long identifier);

    Set<Film> getFilmsChart();
}
