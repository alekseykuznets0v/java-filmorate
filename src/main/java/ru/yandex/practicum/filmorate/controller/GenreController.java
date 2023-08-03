package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.genre.GenreService;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
@Slf4j
public class GenreController {
    private final GenreService genreService;

    @GetMapping(("/{id}"))
    public Genre getGenreById(@PathVariable Integer id) {
        log.info(String.format("Получен GET запрос к жанрам с переменной %s", id));
        return genreService.getGenreById(id);
    }

    @GetMapping
    public Collection<Genre> getAllGenres() {
        log.info("Получен GET запрос на получение списка всех жанров");
        return genreService.getAllGenres();
    }
}
