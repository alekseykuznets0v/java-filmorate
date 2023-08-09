package ru.yandex.practicum.filmorate.storage.dao.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
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
    private static final String FROM_GENRES = "FROM genres ";

    @Override
    public Genre getGenreById(int id) {
        isGenreIdExist(id);
        log.info("В БД отправлен запрос getGenreById с параметром: " + id);
        String request = "SELECT * " +
                         FROM_GENRES +
                         "WHERE id = ?";

        return jdbcTemplate.queryForObject(request, (rs, rowNum) -> makeGenre(rs), id);
    }

    @Override
    public Collection<Genre> getAllGenres() {
        log.info("В БД отправлен запрос getAllGenres");
        String request = "SELECT * " +
                         FROM_GENRES +
                         "ORDER BY id";
        return jdbcTemplate.query(request, (rs, rowNum) -> makeGenre(rs));
    }

    @Override
    public Set<Genre> getGenresByFilmId(Long filmId) {
        log.info("В БД отправлен запрос getGenresByFilmId c параметром: " + filmId);
        String request = "SELECT g.id, g.name " +
                         "FROM films AS f " +
                         "JOIN film_genres AS fg ON f.id = fg.film_id " +
                         "JOIN genres AS g ON fg.genre_id = g.id " +
                         "WHERE f.id = ?";
        return new HashSet<>(jdbcTemplate.query(request, (rs, rowNum) -> makeGenre(rs), filmId));
    }

    private void isGenreIdExist(Integer id) {
        log.info("В БД отправлен запрос isGenreIdExist с параметром: " + id);
        String request = "SELECT id " +
                         FROM_GENRES +
                         "WHERE id = ?";
        SqlRowSet idRows = jdbcTemplate.queryForRowSet(request, id);

        if (!idRows.next()) {
            throw new NotFoundException(String.format("Жанр с id=%s не найден", id));
        }
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        return new Genre(rs.getInt("id"), rs.getString("name"));
    }
}
