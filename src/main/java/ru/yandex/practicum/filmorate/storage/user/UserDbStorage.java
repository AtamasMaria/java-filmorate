package ru.yandex.practicum.filmorate.storage.user;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Component("UserDbStorage")
@AllArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User create(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) " +
                "VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"user_id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);

        int id = Objects.requireNonNull(keyHolder.getKey()).intValue();
        user.setId(id);

        return user;
    }

    @Override
    public User update(User user) {
      checkUserExist(user.getId());
        String sql2 = "UPDATE users SET email=?, login=?, name=?, birthday=? WHERE user_id=?";
        jdbcTemplate.update(sql2, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return getUserById(user.getId());
    }

    @Override
    public List<User> findAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new User(
                        rs.getInt("user_id"),
                        rs.getString("email"),
                        rs.getString("login"),
                        rs.getString("name"),
                        Objects.requireNonNull(rs.getDate("birthday")).toLocalDate()));
    }

    @Override
    public User getUserById(Integer userId) {
        String sqlUser = "SELECT * FROM users WHERE user_id=?";
        User user;
        try {
            user = jdbcTemplate.queryForObject(sqlUser, (rs, rowNum) ->
                    new User(
                            rs.getInt("user_id"),
                            rs.getString("email"),
                            rs.getString("login"),
                            rs.getString("name"),
                            Objects.requireNonNull(rs.getDate("birthday")).toLocalDate()), userId);
        }
        catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользован с таким id не зарегестрирован.");
        }
        return user;
    }

    @Override
    public Collection<User> getUserFriends(Integer userId) {
        checkUserExist(userId);
        String sql = "select * from users u, friends f where f.user_id = ? and u.user_id = f.friend_id";

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new User(
                        rs.getInt("user_id"),
                        rs.getString("email"),
                        rs.getString("login"),
                        rs.getString("name"),
                        Objects.requireNonNull(rs.getDate("birthday")).toLocalDate()), userId);
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        checkUserExist(userId);
        checkUserExist(friendId);

        String sql = "insert into friends (user_id, friend_id) values (?, ?)";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        checkUserExist(userId);
        checkUserExist(friendId);

        String sql = "DELETE FROM friends WHERE user_id=? and friend_id=?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    public void checkUserExist(Integer id) {
        String sql = "SELECT EXISTS (SELECT * FROM users WHERE user_id = ?)";
        Boolean exists = jdbcTemplate.queryForObject(sql, Boolean.class, id);
        if (!exists) {
            throw new NotFoundException(String.format("Пользователь не был обнаружен."));
        }
    }

    @Override
    public Collection<User> getCommonFriends(Integer userId, Integer otherId) {
        checkUserExist(userId);
        checkUserExist(otherId);

        String sql = "SELECT * FROM users u, friends f, friends o where u.user_id = f.friend_id and u.user_id = o.friend_id " +
                "and f.user_id=? and o.user_id=?";

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                        new User(
                                rs.getInt("user_id"),
                                rs.getString("email"),
                                rs.getString("login"),
                                rs.getString("name"),
                                Objects.requireNonNull(rs.getDate("birthday")).toLocalDate()),
                userId, otherId);
    }
}