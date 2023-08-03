package ru.yandex.practicum.filmorate.storage.dao.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.friends.FriendsDao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component("UserDbStorage")
@RequiredArgsConstructor
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FriendsDao friendsDao;
    private static final String SELECT_ALL = "SELECT * ";
    private static final String FROM_USERS = "FROM users ";
    private static final String WHERE_ID = "WHERE id = ?";

    @Override
    public Collection<User> getAllUsers() {
        log.info("В БД отправлен запрос getAllUsers");
        String request = SELECT_ALL +
                         FROM_USERS;
        return jdbcTemplate.query(request, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public User getUserById(Long id) {
        isUserIdExist(id);
        log.info("В БД отправлен запрос getUserById c параметром " + id);
        String request = SELECT_ALL +
                         FROM_USERS +
                         WHERE_ID;
        return jdbcTemplate.queryForObject(request, (rs, rowNum) -> makeUser(rs), id);
    }

    @Override
    public User addUser(User user) {
        String updateRequest = "INSERT INTO users (email, login, name, birthday)" +
                                "VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement preparedStatement = con.prepareStatement(updateRequest, new String[]{"id"});
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getLogin());
            preparedStatement.setString(3, user.getName());
            preparedStatement.setDate(4, Date.valueOf(user.getBirthday()));
            return preparedStatement;
        }, keyHolder);

        Long userId = Objects.requireNonNull(keyHolder.getKey()).longValue();

        return getUserById(userId);
    }

    @Override
    public User updateUser(User user) {
        isUserIdExist(user.getId());
        log.info("В БД отправлен запрос updateUser c параметром " + user);
        String request = "UPDATE users " +
                         "SET email = ?, " +
                         "login = ?, " +
                         "name = ?, " +
                         "birthday = ? " +
                         WHERE_ID;

        String selectRequest = SELECT_ALL +
                               FROM_USERS +
                               WHERE_ID;

        jdbcTemplate.update(request,
                            user.getEmail(),
                            user.getLogin(),
                            user.getName(),
                            user.getBirthday(),
                            user.getId());

        return jdbcTemplate.queryForObject(selectRequest, (rs, rowNum) -> makeUser(rs), user.getId());
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
    public List<User> getFriends(Long id) {
        log.info("В БД отправлен запрос getFriends c параметром " + id);
        return friendsDao.getFriendsIdByUserId(id).stream()
                .mapToLong(Long::valueOf)
                .mapToObj(this::getUserById)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAllUsers() {
        log.info("В БД отправлен запрос deleteAllUsers");
        String request = "TRUNCATE TABLE users";
        jdbcTemplate.execute(request);
    }

    @Override
    public void deleteUserById(Long id) {
        isUserIdExist(id);
        log.info("В БД отправлен запрос deleteUserById с параметром" + id);
        String request = "DELETE " +
                         FROM_USERS +
                         WHERE_ID;
        jdbcTemplate.update(request, id);
    }

    @Override
    public void addFriend(Long id, Long friendId) {
        isUserIdExist(id);
        isUserIdExist(friendId);
        log.info("В БД отправлен запрос addFriend с параметрами userId=" + id + " и friendId=" + friendId);
        friendsDao.addFriendRequest(id, friendId);
    }

    @Override
    public void deleteFriend(Long id, Long friendId) {
        isUserIdExist(id);
        isUserIdExist(friendId);
        log.info("В БД отправлен запрос deleteFriend с параметрами userId=" + id + " и friendId=" + friendId);
        friendsDao.deleteFriend(id, friendId);
    }

    @Override
    public void isUserIdExist(Long id) {
        log.info("В БД отправлен запрос isUserIdExist с параметром" + id);
        String request = "SELECT id " +
                FROM_USERS +
                WHERE_ID;
        SqlRowSet idRows = jdbcTemplate.queryForRowSet(request, id);

        if(!idRows.next()) {
            throw new NotFoundException(String.format("Пользователь с id=%s не найден", id));
        }
    }

    private User makeUser (ResultSet rs) throws SQLException {
        Long userId = rs.getLong("id");
        return User.builder()
                .id(userId)
                .name(rs.getString("name"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .friends(friendsDao.getFriendsIdByUserId(userId))
                .build();
    }
}
