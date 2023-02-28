package ru.yandex.practicum.filmorate.storage.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
public class UserDBStorage implements UserStorage{
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User create(User user) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("users").usingGeneratedKeyColumns("user_id");
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("email", user.getEmail())
                .addValue("login", user.getLogin())
                .addValue("name", user.getName())
                .addValue("birthday", user.getBirthday());
        Number num = jdbcInsert.executeAndReturnKey(parameters);
        user.setId(num.intValue());
        log.info("Пользователь добавлен: {} {}", user.getId(), user.getName());
        return user;
    }

    @Override
    public User update(User user) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from USERS where USER_ID = ?", user.getId());
        if (!userRows.next()) {
            throw new NotFoundException("Такого пользователя нет");
        }
        String sql = " update USERS SET NAME = ?, LOGIN = ?, EMAIL = ?, BIRTHDAY = ?" +
                "where USER_ID = ?";
        jdbcTemplate.update(sql,
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                user.getBirthday(),
                user.getId()
        );
        log.info("Пользователь обновлен: {} {}", user.getId(), user.getName());
        return user;
    }

    @Override
    public List<User> findAllUsers() {
        String sqlQuery = "select * from users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User getUserById(Integer userId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from USERS where USER_ID = ?", userId);
        if (!userRows.next()) {
            throw new NotFoundException("Такого пользователя нет");
        }
        String sqlQuery = "select * from USERS where USER_ID = ?";
        User user = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, userId);
        log.info("Пользователь найден: {} {}", user.getId(), user.getName());
        return user;
    }

    @Override
    public List<User> getUserFriends(Integer userId) {
        List<User> friends = new ArrayList<>();
        User user;
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select FRIEND_ID from FRIENDS" +
                " where USER_ID = ?", userId);
        while (userRows.next()) {
            user = getUserById(userRows.getInt("FRIEND_ID"));
            friends.add(user);
        }
        return friends;

    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer friendId) {
        List<User> commonFriends = new ArrayList<>();
        User user;
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select FRIEND_ID from FRIENDS" +
                " where USER_ID = ? and FRIEND_ID in (select FRIEND_ID from FRIENDS" +
                " where USER_ID = ? )", userId, friendId);
        while (userRows.next()) {
            user = getUserById(userRows.getInt("FRIEND_ID"));
            commonFriends.add(user);
        }
        return commonFriends;
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return new User(
                resultSet.getInt("user_id"),
                resultSet.getString("email"),
                resultSet.getString("login"),
                resultSet.getString("name"),
                resultSet.getDate("birthday").toLocalDate()
        );
    }

    @Override
    public User addToFriend(int userId, int friendId){
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from USERS where USER_ID = ?", userId);
        if (!userRows.next()) {
            throw new NotFoundException("Такого пользователя нет");
        }
        SqlRowSet friendRows = jdbcTemplate.queryForRowSet("select * from USERS where USER_ID = ?", friendId);
        if (!userRows.next()) {
            throw new NotFoundException("Такого пользователя нет");
        }
        User user = getUserById(userId);
        String sql = "insert into FRIENDS(FRIEND_ID, USER_ID, STATUS) values ( ?,?,'true')";
        jdbcTemplate.update(sql, friendId, userId);
        return user;
    }

    @Override
    public User deleteFromFriend(int userId, int friendId) {
        String sql = "delete  from FRIENDS where USER_ID = ? and FRIEND_ID = ?";
        jdbcTemplate.update(sql, userId, friendId);
        return getUserById(userId);
    }

}
