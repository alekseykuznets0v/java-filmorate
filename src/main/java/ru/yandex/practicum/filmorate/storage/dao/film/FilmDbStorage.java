package ru.yandex.practicum.filmorate.storage.dao.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
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
import java.util.Objects;
import java.util.Set;

@Component("FilmDbStorage")
@RequiredArgsConstructor
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
        return jdbcTemplate.query(request, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film getFilmById(Long id) {
        isFilmIdExist(id);
        String request = SELECT_ALL +
                         FROM_FILMS +
                         WHERE_ID;
        return jdbcTemplate.queryForObject(request, (rs, rowNum) -> makeFilm(rs), id);
    }

    @Override
    public Film addFilm(Film film) {
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

        return jdbcTemplate.queryForObject(selectRequest, (rs, rowNum) -> makeFilm(rs), film.getId());
    }

    @Override
    public void deleteAllFilms() {
        String request = "TRUNCATE TABLE films";
        jdbcTemplate.execute(request);
    }

    @Override
    public void deleteFilmById(Long id) {
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
        throw new UnsupportedOperationException("Операция setIdentifier для фильмов не поддерживается");
    }

    @Override
    public void isFilmIdExist(Long id) {
        String request = "SELECT id " +
                         FROM_FILMS +
                         WHERE_ID;
        SqlRowSet idRows = jdbcTemplate.queryForRowSet(request, id);

        if(!idRows.next()) {
            throw new NotFoundException(String.format("Фильм с id=%s не найден", id));
        }
    }

    private void addGenresForFilm (Long filmId, Set<Genre> genres) {
        if(!genres.isEmpty()) {
            String request = "INSERT INTO film_genres (film_id, genre_id) " +
                             "VALUES (?, ?)";
            genres.forEach(genre -> jdbcTemplate.update(request, filmId, genre.getId()));
        }
    }

    private Film makeFilm (ResultSet rs) throws SQLException {
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
}
