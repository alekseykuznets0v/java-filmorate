package ru.yandex.practicum.filmorate.storage.dao.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
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
        return null;
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
    public void setIdentifier(long identifier) {

    }

    @Override
    public void deleteAllFilms() {

    }

    @Override
    public boolean isFilmIdExist(Long id) {
        return false;
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
