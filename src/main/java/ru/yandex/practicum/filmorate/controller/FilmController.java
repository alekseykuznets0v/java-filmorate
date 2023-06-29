package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;
import java.util.HashMap;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private int identifier = 0;
    private final HashMap<Integer, Film> films = new HashMap<>();

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) throws ValidationException {
        if (films.containsKey(film.getId()) || films.containsValue(film)) {
            log.warn(ValidationException.class + ": В базе данных уже есть такой фильм");
            throw new ValidationException(String.format("Фильм с id=%s уже существует", film.getId()));
        }
        film.setId(getIdentifier());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        } else {
            log.warn(ValidationException.class + ": В библиотеке нет такого фильма");
            throw new ValidationException(String.format("В библиотеке нет фильма с id=%s", film.getId()));
        }
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    private int getIdentifier() {
        return ++identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public HashMap<Integer, Film> getFilms() {
        return films;
    }
}
