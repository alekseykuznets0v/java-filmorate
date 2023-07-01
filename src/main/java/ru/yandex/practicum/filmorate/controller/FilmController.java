package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController extends Controller<Film> {

    @PostMapping
    public Film add(@Valid @RequestBody Film film) throws ValidationException {
        log.info(String.format("Получен POST запрос с телом %s", film));

        if (storage.containsValue(film)) {
            String warning = String.format("Такой фильм уже существует, id=%s", film.getId());
            log.warn(ValidationException.class + ": " + warning);
            throw new ValidationException(warning);
        }
        film.setId(getIdentifier());
        storage.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) throws ValidationException {
        log.info(String.format("Получен PUT запрос с телом %s", film));

        if (storage.containsKey(film.getId())) {
            storage.put(film.getId(), film);
            return film;
        } else {
            String warning = String.format("В базе данных нет фильма с id=%s", film.getId());
            log.warn(ValidationException.class + ": " + warning);
            throw new ValidationException(warning);
        }
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Получен GET запрос к эндпоинту /films");
        return getAllValues();
    }
}
