package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.mpa.MpaService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
@Slf4j
public class MpaController {
    private final MpaService mpaService;

    @GetMapping(("/{id}"))
    public MpaRating getMpaById(@PathVariable Integer id) {
        log.info(String.format("Получен GET запрос к рейтингам mpa с переменной %s", id));
        return mpaService.getMpaById(id);
    }

    @GetMapping
    public Collection<MpaRating> getAllMpa() {
        log.info("Получен GET запрос на получение списка всех рейтингов mpa");
        return mpaService.getAllMpa();
    }
}
