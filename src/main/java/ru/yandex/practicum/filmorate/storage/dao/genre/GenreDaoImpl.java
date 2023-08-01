package ru.yandex.practicum.filmorate.storage.dao.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class GenreDaoImpl implements GenreDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre getGenreById(int id) {
        String request = "SELECT * " +
                         "FROM genres " +
                         "WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(request, (rs, rowNum) -> makeGenre(rs), id);
        } catch (DataAccessException e) {
            String message = String.format("Жанр с id=%s не найден", id);
            log.warn(message);
            throw new NotFoundException(message);
        }
    }

    @Override
    public Collection<Genre> getAllGenres() {
        String request = "SELECT * " +
                         "FROM genres " +
                         "ORDER BY id";
        return jdbcTemplate.query(request, (rs, rowNum) -> makeGenre(rs));
    }

    @Override
    public Set<Genre> getGenresByFilmId(Long filmId) {
        String request = "SELECT g.id, g.name " +
                         "FROM films AS f " +
                         "JOIN film_genres AS fg ON f.id = fg.film_id " +
                         "JOIN genres AS g ON fg.genre_id = g.id " +
                         "WHERE f.id = ?";
        Set<Genre> genres = new HashSet<>(jdbcTemplate.query(request, (rs, rowNum) -> makeGenre(rs), filmId));
        return genres;
    }

    private Genre makeGenre (ResultSet rs) throws SQLException {
        return new Genre(rs.getInt("id"), rs.getString("name"));
    }
}
