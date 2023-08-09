package ru.yandex.practicum.filmorate.storage.dao.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.AlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.genre.GenreDao;
import ru.yandex.practicum.filmorate.storage.dao.like.LikeDao;
import ru.yandex.practicum.filmorate.storage.dao.mpa.MpaDao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component("FilmDbStorage")
@RequiredArgsConstructor
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaDao mpaDao;
    private final LikeDao likeDao;
    private final GenreDao genreDao;
    private static final String SELECT_ALL = "SELECT * ";
    private static final String FROM_FILMS = "FROM films ";
    private static final String WHERE_ID = "WHERE id = ?";


    @Override
    public Collection<Film> getAllFilms() {
        String request = SELECT_ALL +
                         FROM_FILMS;
        log.info("В БД отправлен запрос getAllFilms");
        return jdbcTemplate.query(request, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film getFilmById(Long id) {
        isFilmIdExist(id);
        String request = SELECT_ALL +
                         FROM_FILMS +
                         WHERE_ID;
        log.info("В БД отправлен запрос getFilmById c параметром " + id);
        return jdbcTemplate.queryForObject(request, (rs, rowNum) -> makeFilm(rs), id);
    }

    @Override
    public Film addFilm(Film film) {
        log.info("В БД отправлен запрос addFilm c параметром " + film);
        isFilmExist(film);
        String updateRequest = "INSERT INTO films (name, description, release_date, duration, mpa_id)" +
                                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement preparedStatement = con.prepareStatement(updateRequest, new String[]{"id"});
            preparedStatement.setString(1, film.getName());
            preparedStatement.setString(2, film.getDescription());
            preparedStatement.setDate(3, Date.valueOf(film.getReleaseDate()));
            preparedStatement.setInt(4, film.getDuration());
            preparedStatement.setInt(5, film.getMpa().getId());
            return preparedStatement;
        }, keyHolder);

        Long filmId = Objects.requireNonNull(keyHolder.getKey()).longValue();

        addGenresForFilm(filmId, film.getGenres());

        return getFilmById(filmId);
    }

    @Override
    public Film updateFilm(Film film) {
        log.info("В БД отправлен запрос updateFilm c параметром " + film);
        isFilmIdExist(film.getId());
        String request = "UPDATE films " +
                         "SET name = ?, " +
                         "description = ?, " +
                         "release_date = ?, " +
                         "duration = ?, " +
                         "mpa_id = ? " +
                         WHERE_ID;

        String selectRequest = SELECT_ALL +
                               FROM_FILMS +
                               WHERE_ID;

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
        String request = "DELETE " + FROM_FILMS;
        jdbcTemplate.execute(request);
    }

    @Override
    public void deleteFilmById(Long id) {
        log.info("В БД отправлен запрос deleteFilmById с параметром " + id);
        isFilmIdExist(id);
        String request = "DELETE " +
                         FROM_FILMS +
                         WHERE_ID;
        jdbcTemplate.update(request, id);
    }

    /*  Метод setIdentifier нужен для тестирования реализации InMemoryFilmStorage,
        если функционал хранения данных в оперативной памяти будет не актуален,
        то метод можно удалить из интерфейса и его реализации, а также сервиса и контроллера
    */
    @Override
    public void setIdentifier(long identifier) {
        log.info("Из сервиса запрошен неподдерживаемый метод setIdentifier");
        throw new UnsupportedOperationException("Операция setIdentifier для фильмов не поддерживается");
    }

    @Override
    public void isFilmIdExist(Long id) {
        log.info("В БД отправлен запрос isFilmIdExist с параметром " + id);
        String request = "SELECT id " +
                         FROM_FILMS +
                         WHERE_ID;
        SqlRowSet idRows = jdbcTemplate.queryForRowSet(request, id);

        if (!idRows.next()) {
            throw new NotFoundException(String.format("Фильм с id=%s не найден", id));
        }
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
    public void addLike(Long userId, Long filmId) {
        isFilmIdExist(filmId);
        likeDao.addLike(filmId, userId);
    }

    @Override
    public void deleteLike(Long userId, Long filmId) {
        isFilmIdExist(filmId);
        likeDao.deleteLike(filmId, userId);
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
                   .likes(likeDao.getLikesByFilmId(filmId))
                   .genres(genreDao.getGenresByFilmId(filmId))
                   .build();
    }

    private void isFilmExist(Film film) {
        log.info("В БД отправлен запрос isFilmExist с параметром" + film);
        String request = "SELECT id, name, description, duration, release_date " +
                FROM_FILMS +
                "WHERE name = ? " +
                "AND description = ? " +
                "AND duration = ? " +
                "AND release_date = ?";
        SqlRowSet idRows = jdbcTemplate.queryForRowSet(request,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate());

        if (idRows.next()) {
            throw new AlreadyExistsException("Такой фильм уже существует в БД");
        }
    }
}
