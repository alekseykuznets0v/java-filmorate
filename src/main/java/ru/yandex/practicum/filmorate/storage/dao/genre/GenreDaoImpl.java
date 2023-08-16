package ru.yandex.practicum.filmorate.storage.dao.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class GenreDaoImpl implements GenreDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Genre> getGenreById(int id) {
        log.info("В БД отправлен запрос getGenreById с параметром: " + id);
        String request = "SELECT * " +
                         "FROM genres " +
                         "WHERE id = ?";

        SqlRowSet rs = jdbcTemplate.queryForRowSet(request, id);

        if (rs.next()) {
            return Optional.of(new Genre(rs.getInt("id"), rs.getString("name")));
        }

        return Optional.empty();
    }

    @Override
    public Collection<Genre> getAllGenres() {
        log.info("В БД отправлен запрос getAllGenres");
        String request = "SELECT * " +
                         "FROM genres " +
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
                         "WHERE f.id = ? " +
                         "ORDER BY g.id";
        return new HashSet<>(jdbcTemplate.query(request, (rs, rowNum) -> makeGenre(rs), filmId));
    }

    @Override
    public Map<Long, Set<Genre>> getGenresForAllFilms() {
        log.info("В БД отправлен запрос getGenresForAllFilms");
        String request = "SELECT fg.film_id AS film_id, " +
                         "g.id AS genre_id, " +
                         "g.name AS genre_name " +
                         "FROM film_genres AS fg " +
                         "LEFT JOIN genres AS g ON fg.genre_id = g.id ";
        Map<Long, Set<Genre>> allFilmsGenres = new HashMap<>();

        jdbcTemplate.query(request, rs -> {
            Genre genre = new Genre(rs.getInt("genre_id"), rs.getString("genre_name"));
            Long filmId = rs.getLong("film_id");

            allFilmsGenres.putIfAbsent(filmId, new HashSet<>());
            allFilmsGenres.get(filmId).add(genre);
        });

        return allFilmsGenres;
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        return new Genre(rs.getInt("id"), rs.getString("name"));
    }
}
