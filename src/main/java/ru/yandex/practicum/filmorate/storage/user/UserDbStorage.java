package ru.yandex.practicum.filmorate.storage.user;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

@Component("UserDbStorage")
@AllArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User create(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, user.getEmail());
                    ps.setString(2, user.getLogin());
                    ps.setString(3, user.getName());
                    ps.setDate(4, Date.valueOf(user.getBirthday()));
                    return ps;
                },
                keyHolder);

        int id = Objects.requireNonNull(keyHolder.getKey()).intValue();

        if (user.getFriendsIds() != null) {
            for (Integer friendId : user.getFriendsIds()) {
                addFriend(user.getId(), friendId);
            }
        }
        return getUserById(id);
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users SET email=?, login=?, name=?, birthday=? WHERE user_id=?";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
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
                        Objects.requireNonNull(rs.getDate("birthday")).toLocalDate(),
                        getUserFriends(rs.getInt("user_id"))));
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
                            Objects.requireNonNull(rs.getDate("birthday")).toLocalDate(),
                            getUserFriends(rs.getInt("user_id"))), userId);
        }
        catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("User", "Пользован с таким id не зарегестрирован.");
        }
        return user;
    }

    @Override
    public List<Integer> getUserFriends(Integer userId) {
        String sqlGetFriends = "SELECT friend_id FROM friends where user_id=?";
        return jdbcTemplate.queryForList(sqlGetFriends, Integer.class, userId);
    }

    @Override
    public boolean addFriend(int userId, int friendId) {
        boolean isFriend;
        String sql = "SELECT * FROM friends WHERE user_id=? AND friend_id=?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, friendId, userId);
        String sqlSetFriend = "INSERT INTO friends (user_id, friend_id, status) VALUES (?,?,?)";
        isFriend = sqlRowSet.next();
        jdbcTemplate.update(sqlSetFriend, userId, friendId, isFriend);
        if (isFriend) {
            String sqlSetStatus = "UPDATE friends SET status=true WHERE user_id=? AND friend_id=?";
            jdbcTemplate.update(sqlSetStatus, friendId, userId);
        }
        return true;
    }

    @Override
    public boolean deleteFriend(int userId, int friendId) {
        String sql = "DELETE FROM friends WHERE user_id=? AND friend_id=?";
        jdbcTemplate.update(sql, userId, friendId);
        String sqlSetStatus = "UPDATE friends SET status=false WHERE user_id=? AND friend_id=?";
        jdbcTemplate.update(sqlSetStatus, friendId, userId);
        return true;
    }
}
