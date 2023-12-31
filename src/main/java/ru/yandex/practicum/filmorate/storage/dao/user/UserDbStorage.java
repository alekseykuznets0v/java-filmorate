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
import ru.yandex.practicum.filmorate.storage.dao.friends.FriendsDao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FriendsDao friendsDao;

    @Override
    public Collection<User> getAllUsers() {
        log.info("В БД отправлен запрос getAllUsers");
        String request = "SELECT * " +
                         "FROM users";

        Collection<User> users = jdbcTemplate.query(request, (rs, rowNum) -> makeUserWithoutFriends(rs));
        Map<Long, Set<Long>> allUsersFriends = friendsDao.getFriendsIdForAllUsers();

        users.forEach(user -> user.setFriends(allUsersFriends.getOrDefault(user.getId(), new HashSet<>())));

        return users;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        log.info("В БД отправлен запрос getUserById c параметром " + id);
        String request = "SELECT * " +
                         "FROM users " +
                         "WHERE id = ?";

        SqlRowSet rs = jdbcTemplate.queryForRowSet(request, id);

        if (rs.next()) {
            return Optional.of(User.builder()
                    .id(id)
                    .name(rs.getString("name"))
                    .email(rs.getString("email"))
                    .login(rs.getString("login"))
                    .birthday(Objects.requireNonNull(rs.getDate("birthday")).toLocalDate())
                    .friends(friendsDao.getFriendsIdByUserId(id))
                    .build());
        }

        return Optional.empty();
    }

    @Override
    public Optional<User> addUser(User user) {
        if (isEmailExist(user.getEmail())) {
            return Optional.empty();
        }

        log.info("В БД отправлен запрос addUser c параметром " + user);
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
        log.info("В БД отправлен запрос updateUser c параметром " + user);
        String request = "UPDATE users " +
                         "SET email = ?, " +
                         "login = ?, " +
                         "name = ?, " +
                         "birthday = ? " +
                         "WHERE id = ?";

        String selectRequest = "SELECT * " +
                               "FROM users " +
                               "WHERE id = ?";

        jdbcTemplate.update(request,
                            user.getEmail(),
                            user.getLogin(),
                            user.getName(),
                            user.getBirthday(),
                            user.getId());

        return jdbcTemplate.queryForObject(selectRequest, (rs, rowNum) -> makeUser(rs), user.getId());
    }

    @Override
    public List<Optional<User>> getFriends(Long id) {
        log.info("В БД отправлен запрос getFriends c параметром " + id);
        return friendsDao.getFriendsIdByUserId(id).stream()
                .mapToLong(Long::valueOf)
                .mapToObj(this::getUserById)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAllUsers() {
        log.info("В БД отправлен запрос deleteAllUsers");
        String request = "DELETE " + "FROM users";
        jdbcTemplate.execute(request);
    }

    @Override
    public void deleteUserById(Long id) {
        log.info("В БД отправлен запрос deleteUserById с параметром" + id);
        String request = "DELETE " +
                         "FROM users " +
                         "WHERE id = ?";
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
        log.info("В БД отправлен запрос isUserIdExist с параметром " + id);
        String request = "SELECT id " +
                         "FROM users " +
                         "WHERE id = ?";
        SqlRowSet idRows = jdbcTemplate.queryForRowSet(request, id);

        if (!idRows.next()) {
            throw new NotFoundException(String.format("Пользователь с id=%s не найден", id));
        }
    }

    private boolean isEmailExist(String email) {
        log.info("В БД отправлен запрос isUserIdExist с параметром " + email);
        String request = "SELECT id " +
                         "FROM users " +
                         "WHERE email=?";
        SqlRowSet idRows = jdbcTemplate.queryForRowSet(request, email);

        return idRows.next();
    }

    private User makeUser(ResultSet rs) throws SQLException {
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

    private User makeUserWithoutFriends(ResultSet rs) throws SQLException {
        Long userId = rs.getLong("id");
        return User.builder()
                .id(userId)
                .name(rs.getString("name"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }
}
