package ru.yandex.practicum.filmorate.storage.dao.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.genre.GenreDao;
import ru.yandex.practicum.filmorate.storage.dao.like.LikeDao;
import ru.yandex.practicum.filmorate.storage.dao.mpa.MpaDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component("FilmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaDao mpaDao;
    private final LikeDao likeDao;
    private final GenreDao genreDao;

    @Override
    public Collection<Film> getAllFilms() {
        String request = "SELECT *," +
                         "FROM films";
        return jdbcTemplate.query(request, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film getFilmById(Long id) {
        isFilmIdExist(id);
        String request = "SELECT *," +
                         "FROM films" +
                         "WHERE id = ?";
        return jdbcTemplate.queryForObject(request, (rs, rowNum) -> makeFilm(rs), id);
    }

    @Override
    public Film addFilm(Film film) {
        return null;
    }

    @Override
    public Film updateFilm(Film film) {
        return null;
    }

    @Override
    public void deleteAllFilms() {

    }

    @Override
    public void setIdentifier(long identifier) {
        throw new UnsupportedOperationException("Эта операция не поддерживается");
    }

    @Override
    public void isFilmIdExist(Long id) {
        String request = "SELECT id," +
                         "FROM films" +
                         "WHERE id = ?";
        SqlRowSet idRows = jdbcTemplate.queryForRowSet(request, id);

        if(!idRows.next()) {
            throw new NotFoundException(String.format("Фильм с id=%s не найден", id));
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
