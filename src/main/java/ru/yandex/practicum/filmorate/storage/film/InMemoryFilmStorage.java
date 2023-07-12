package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.AlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;

import java.util.Collection;
import java.util.Map;
import java.util.OptionalLong;

@Component
@Slf4j
public class InMemoryFilmStorage extends InMemoryStorage<Film> implements FilmStorage {

    @Override
    public Collection<Film> getAllFilms() {
        return storage.values();
    }

    @Override
    public Map<Long, Film> getStorage() {
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
        if (storage.containsValue(film)){
            OptionalLong id = storage.entrySet().stream().filter(entry -> film.equals(entry.getValue())).mapToLong(Map.Entry::getKey).findFirst();
            String warning = String.format("Такой фильм уже существует, id=%s", id.isPresent() ? id.getAsLong() : null);
            log.warn(AlreadyExistsException.class + ": " + warning);
            throw new AlreadyExistsException(warning);
        }
        long id = getIdentifier();
        film.setId(id);
        storage.put(id, film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (storage.containsKey(film.getId())) {
            storage.put(film.getId(), film);
            return storage.get(film.getId());
        } else {
            String warning = String.format("В базе данных отсутствует фильм с id=%s", film.getId());
            log.warn(NotFoundException.class + ": " + warning);
            throw new NotFoundException(warning);
        }
    }

    @Override
    public void setIdentifier(long identifier) {
        this.identifier = identifier;
    }
}
