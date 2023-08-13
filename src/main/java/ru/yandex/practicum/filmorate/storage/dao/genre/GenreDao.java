package ru.yandex.practicum.filmorate.storage.dao.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface GenreDao {
    Optional<Genre> getGenreById(int id);

    Collection<Genre> getAllGenres();

    Set<Genre> getGenresByFilmId(Long filmId);

    Map<Long, Set<Genre>> getGenresForAllFilms();
}
