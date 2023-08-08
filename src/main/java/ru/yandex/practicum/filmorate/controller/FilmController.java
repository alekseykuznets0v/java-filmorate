package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
@Getter
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmServiceImpl;

    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        log.info(String.format("Получен POST запрос с телом %s", film));
        return filmServiceImpl.addFilm(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info(String.format("Получен PUT запрос с телом %s", film));
        return filmServiceImpl.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Получен PUT запрос к эндпоинту /films/id/like/userId");
        filmServiceImpl.addLike(userId, id);
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Получен GET запрос к эндпоинту /films");
        return filmServiceImpl.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        log.info("Получен GET запрос к эндпоинту /films/id");
        return filmServiceImpl.getFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(name = "count", defaultValue = "10") Integer count) {
        log.info("Получен GET запрос к эндпоинту /films/popular?count=" + count);
        return filmServiceImpl.getMostPopularFilms(count);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Получен DELETE запрос к эндпоинту /films/id/like/userId");
        filmServiceImpl.deleteLike(userId, id);
    }
}
