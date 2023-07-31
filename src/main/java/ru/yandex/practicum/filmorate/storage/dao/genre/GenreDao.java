package ru.yandex.practicum.filmorate.storage.dao.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Set;

public interface GenreDao {
    Genre getGenreById (int id);

    Collection<Genre> getAllGenres();

    Set<Genre> getGenresByFilmId(Long filmId);
}
