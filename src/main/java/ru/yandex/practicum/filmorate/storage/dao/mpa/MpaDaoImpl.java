package ru.yandex.practicum.filmorate.storage.dao.mpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
@RequiredArgsConstructor
@Slf4j
public class MpaDaoImpl implements MpaDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public MpaRating getMpaById(int id) {
        String request = "SELECT * " +
                         "FROM mpa " +
                         "WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(request, (rs, rowNum) -> makeMpaRating(rs), id);
        } catch (DataAccessException e) {
            String message = String.format("Рейтинг с id=%s не найден", id);
            log.warn(message);
            throw new NotFoundException(message);
        }
    }

    @Override
    public Collection<MpaRating> getAllMpa() {
        String request = "SELECT * " +
                         "FROM mpa" +
                         "ORDER BY id";
        return jdbcTemplate.query(request, (rs, rowNum) -> makeMpaRating(rs));
    }

    private MpaRating makeMpaRating (ResultSet rs) throws SQLException {
        return new MpaRating(rs.getInt("id"), rs.getString("name"));
    }
}
