package ru.yandex.practicum.filmorate.storage.dao.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.genre.GenreDao;
import ru.yandex.practicum.filmorate.storage.dao.like.LikeDao;
import ru.yandex.practicum.filmorate.storage.dao.mpa.MpaDao;

import java.sql.*;
import java.sql.Date;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaDao mpaDao;
    private final LikeDao likeDao;
    private final GenreDao genreDao;

    @Override
    public Collection<Film> getAllFilms() {
        String request = "SELECT * " +
                         "FROM films";
        log.info("В БД отправлен запрос getAllFilms");

        Collection<Film> films = jdbcTemplate.query(request, (rs, rowNum) -> makeFilmWithoutGenres(rs));
        Map<Long, Set<Genre>> allFilmsGenres = genreDao.getGenresForAllFilms();

        films.forEach(film -> film.setGenres(allFilmsGenres.getOrDefault(film.getId(), new HashSet<>())));

        return films;
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        log.info("В БД отправлен запрос getFilmById c параметром " + id);
        String request = "SELECT * " +
                         "FROM films " +
                         "WHERE id=?";

        SqlRowSet rs = jdbcTemplate.queryForRowSet(request, id);

        if (rs.next()) {
            return Optional.of(Film.builder()
                    .id(id)
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .releaseDate(Objects.requireNonNull(rs.getDate("release_date")).toLocalDate())
                    .duration(rs.getInt("duration"))
                    .mpa(mpaDao.getMpaById(rs.getInt("mpa_id")))
                    .likesNumber(likeDao.getLikesNumberByFilmId(id))
                    .genres(genreDao.getGenresByFilmId(id))
                    .build());
        }

        return Optional.empty();
    }

    @Override
    public Optional<Film> addFilm(Film film) {
        log.info("В БД отправлен запрос addFilm c параметром " + film);

        if (isFilmExist(film)) {
            return Optional.empty();
        }

        String updateRequest = "INSERT INTO films (name, description, release_date, duration, mpa_id)" +
                                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement preparedStatement = con.prepareStatement(updateRequest, new String[]{"id"});
            preparedStatement.setString(1, film.getName());
            preparedStatement.setString(2, film.getDescription());
            preparedStatement.setDate(3, Date.valueOf(film.getReleaseDate()));
            preparedStatement.setInt(4, film.getDuration());
            preparedStatement.setLong(5, film.getMpa().getId());
            return preparedStatement;
        }, keyHolder);

        Long filmId = Objects.requireNonNull(keyHolder.getKey()).longValue();

        addGenresForFilm(filmId, film.getGenres());

        return getFilmById(filmId);
    }

    @Override
    public Film updateFilm(Film film) {
        log.info("В БД отправлен запрос updateFilm c параметром " + film);

        String request = "UPDATE films " +
                         "SET name = ?, " +
                         "description = ?, " +
                         "release_date = ?, " +
                         "duration = ?, " +
                         "mpa_id = ? " +
                         "WHERE id = ?";

        String selectRequest = "SELECT f.id, " +
                               "f.name, " +
                               "f.description, " +
                               "f.release_date, " +
                               "f.duration, " +
                               "f.mpa_id, " +
                               "FROM films AS f " +
                               "WHERE id = ?";

        jdbcTemplate.update(request,
                            film.getName(),
                            film.getDescription(),
                            film.getReleaseDate(),
                            film.getDuration(),
                            film.getMpa().getId(),
                            film.getId());

        updateGenresForFilm(film.getId(), film.getGenres());

        return jdbcTemplate.queryForObject(selectRequest, (rs, rowNum) -> makeFilm(rs), film.getId());
    }

    @Override
    public void deleteAllFilms() {
        log.info("В БД отправлен запрос deleteAllFilms");
        String request = "DELETE FROM films";
        jdbcTemplate.execute(request);
    }

    @Override
    public int deleteFilmById(Long id) {
        log.info("В БД отправлен запрос deleteFilmById с параметром " + id);
        String request = "DELETE " +
                         "FROM films" +
                         "WHERE id=?";
        return jdbcTemplate.update(request, id);
    }

    @Override
    public List<Film> getMostPopularFilms(Integer count) {
        log.info("В БД отправлен запрос getMostPopularFilms с параметром " + count);
        String request = "SELECT f.id, " +
                         "f.name, " +
                         "f.description, " +
                         "f.release_date, " +
                         "f.duration, " +
                         "f.mpa_id, " +
                         "COUNT(l.user_id) AS film_likes " +
                         "FROM films AS f " +
                         "LEFT JOIN likes AS l ON f.id = l.film_id " +
                         "GROUP BY f.id " +
                         "ORDER BY film_likes DESC " +
                         "LIMIT ?";

        return jdbcTemplate.query(request, (rs, rowNum) -> makeFilm(rs), count);
    }

    @Override
    public int addLike(Long userId, Long filmId) {
        return likeDao.addLike(filmId, userId);
    }

    @Override
    public int deleteLike(Long userId, Long filmId) {
        return likeDao.deleteLike(filmId, userId);
    }

    private void addGenresForFilm(Long filmId, Set<Genre> genres) {
        if (!genres.isEmpty()) {
            log.info("В БД отправлен запрос addGenresForFilm с параметрами filmId=" + filmId + " и genres=" + genres);
            String request = "INSERT INTO film_genres (film_id, genre_id) " +
                             "VALUES (?, ?)";
            genres.forEach(genre -> jdbcTemplate.update(request, filmId, genre.getId()));
        }
    }

    private void updateGenresForFilm(Long filmId, Set<Genre> genres) {
        log.info("В БД отправлен запрос updateGenresForFilm с параметрами filmId=" + filmId + " и genres=" + genres);
        String request = "DELETE FROM film_genres " +
                         "WHERE film_id = ?";

        jdbcTemplate.update(request, filmId);
        addGenresForFilm(filmId, genres);
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Long filmId = rs.getLong("id");
        return Film.builder()
                .id(filmId)
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(mpaDao.getMpaById(rs.getInt("mpa_id")))
                .likesNumber(likeDao.getLikesNumberByFilmId(filmId))
                .genres(genreDao.getGenresByFilmId(filmId))
                .build();
    }

    private Film makeFilmWithoutGenres(ResultSet rs) throws SQLException {
        Long filmId = rs.getLong("id");
        return Film.builder()
                .id(filmId)
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(mpaDao.getMpaById(rs.getInt("mpa_id")))
                .likesNumber(likeDao.getLikesNumberByFilmId(filmId))
                .build();
    }

    private boolean isFilmExist(Film film) {
        log.info("В БД отправлен запрос isFilmExist с параметром " + film);
        String request = "SELECT id, name, description, duration, release_date " +
                "FROM films " +
                "WHERE name = ? " +
                "AND description = ? " +
                "AND duration = ? " +
                "AND release_date = ?";
        SqlRowSet idRows = jdbcTemplate.queryForRowSet(request,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate());

        return idRows.next();
    }
}
