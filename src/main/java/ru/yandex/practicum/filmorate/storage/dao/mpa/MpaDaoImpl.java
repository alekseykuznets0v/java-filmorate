package ru.yandex.practicum.filmorate.storage.dao.mpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
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
        isMpaIdExist(id);
        log.info("В БД отправлен запрос getMpaById с параметром" + id);
        String request = "SELECT * " +
                         "FROM mpa " +
                         "WHERE id = ?";
        return jdbcTemplate.queryForObject(request, (rs, rowNum) -> makeMpaRating(rs), id);
    }

    @Override
    public Collection<MpaRating> getAllMpa() {
        log.info("В БД отправлен запрос getAllMpa");
        String request = "SELECT * " +
                         "FROM mpa " +
                         "ORDER BY id";
        return jdbcTemplate.query(request, (rs, rowNum) -> makeMpaRating(rs));
    }

    private void isMpaIdExist(Integer id) {
        log.info("В БД отправлен запрос isMpaIdExist с параметром" + id);
        String request = "SELECT id " +
                         "FROM mpa " +
                         "WHERE id = ?";
        SqlRowSet idRows = jdbcTemplate.queryForRowSet(request, id);

        if (!idRows.next()) {
            throw new NotFoundException(String.format("MPA рейтинг с id=%s не найден", id));
        }
    }

    private MpaRating makeMpaRating(ResultSet rs) throws SQLException {
        return new MpaRating(rs.getLong("id"), rs.getString("name"));
    }
}
