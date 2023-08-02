package ru.yandex.practicum.filmorate.storage.inmemory.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.AlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.inmemory.InMemoryStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Component("InMemoryFilmStorage")
@Slf4j
public class InMemoryFilmStorage extends InMemoryStorage<Film> implements FilmStorage {

    @Override
    public Collection<Film> getAllFilms() {
        return storage.values();
    }

    @Override
    public Film getFilmById(Long id) {
        isFilmIdExist(id);
        return storage.get(id);
    }

    @Override
    public Film addFilm(Film film) {
        if (storage.containsValue(film)) {
            OptionalLong id = storage.entrySet().stream().filter(entry -> film.equals(entry.getValue())).mapToLong(Map.Entry::getKey).findFirst();
            String warning = String.format("Такой фильм уже существует, id=%s", id.isPresent() ? id.getAsLong() : 0);
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
        long id = film.getId();

        isFilmIdExist(id);
        storage.put(id, film);
        return film;
    }

    @Override
    public void setIdentifier(long identifier) {
        this.identifier = identifier;
    }

    @Override
    public void deleteAllFilms() {
        storage.clear();
    }

    @Override
    public void deleteFilmById(Long id) {
        isFilmIdExist(id);
        storage.remove(id);
    }

    @Override
    public void isFilmIdExist(Long id) {
        if (!storage.containsKey(id)) {
            String message = String.format("В базе данных отсутствует фильм с id=%s", id);
            log.warn(message);
            throw new NotFoundException(message);
        }
    }

    @Override
    public List<Film> getMostPopularFilms(Integer count) {
        Set<Film> filmsChart = new TreeSet<>(Comparator.comparing(Film::getLikesNumber).reversed()
                .thenComparing(Film::getId));

        filmsChart.addAll(getAllFilms());

        return filmsChart.stream().limit(count).collect(Collectors.toList());
    }

    @Override
    public void addLike(Long userId, Long filmId) {
        Film film = getFilmById(filmId);
        film.getLikes().add(userId);
        film.increaseLikesNumber();
    }

    @Override
    public void deleteLike(Long userId, Long filmId) {
        Film film = getFilmById(filmId);
        film.getLikes().remove(userId);
        film.decreaseLikesNumber();
    }
}
