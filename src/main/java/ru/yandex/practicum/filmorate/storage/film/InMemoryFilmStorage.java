package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;

import java.util.Collection;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage extends InMemoryStorage<Film> implements FilmStorage {

    @Override
    public Collection<Film> getAllFilms() {
        return storage.values();
    }

    @Override
    public Map<Long, Film> getFilmStorage() {
        return storage;
    }

    @Override
    public Film getFilmById(Long id) {
        if (storage.containsKey(id)) {
            return storage.get(id);
        } else {
            String warning = String.format("В базе данных отсутствует фильм с id=%s", id);
            log.warn(NotFoundException.class + ": " + warning);
            throw new NotFoundException(warning);
        }
    }

    @Override
    public Film addFilm(Film film) {
        long id = getIdentifier();
        film.setId(id);
        return storage.put(id, film);
    }

    @Override
    public Film updateFilm(Film film) {
        if (storage.containsKey(film.getId())) {
            return storage.put(film.getId(), film);
        } else {
            String warning = String.format("В базе данных отсутствует фильм с id=%s", film.getId());
            log.warn(NotFoundException.class + ": " + warning);
            throw new NotFoundException(warning);
        }
    }
}
