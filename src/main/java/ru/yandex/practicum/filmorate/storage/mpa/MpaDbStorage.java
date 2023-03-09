package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;


@Component
@AllArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Mpa> getAllMpa() {
        String sql = "SELECT * FROM mpa";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Mpa(rs.getInt("mpa_id"), rs.getString("name"), rs.getString("description")));
    }

    @Override
    public Mpa getMpaById(int mpaId) {
        SqlRowSet rows = jdbcTemplate.queryForRowSet("SELECT * FROM mpa WHERE mpa_id=?", mpaId);
        if (!rows.next()) {
            throw new NotFoundException("Mpa", "Такого рейтинга нет");
        }
        String sql = "SELECT * FROM mpa WHERE mpa_id=?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
                new Mpa(rs.getInt("mpa_id"), rs.getString("name"),
                        rs.getString("description")), mpaId);
    }
}