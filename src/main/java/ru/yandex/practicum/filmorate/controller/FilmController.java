package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
@Getter
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film add(@Valid @RequestBody Film film) throws ValidationException {
        log.info(String.format("Получен POST запрос с телом %s", film));
        return filmService.getFilmStorage().addFilm(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) throws ValidationException {
        log.info(String.format("Получен PUT запрос с телом %s", film));
        return filmService.getFilmStorage().updateFilm(film);
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Получен GET запрос к эндпоинту /films");
        return filmService.getFilmStorage().getAllFilms();
    }

}
