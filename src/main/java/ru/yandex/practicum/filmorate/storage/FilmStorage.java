package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film add(Film film);
    Film getById(int id);
    Film update(Film film);
    Film deleteById(int id);
    List<Film> getAllFilms();
}
