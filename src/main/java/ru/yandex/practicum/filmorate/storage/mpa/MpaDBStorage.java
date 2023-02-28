package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
public class MpaDBStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public List<Mpa> getMpa(){
        String sql = "select * from rating";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Mpa(rs.getInt("rating_id"), rs.getString("name"),
                        rs.getString("description")));
    }

    public Mpa getMpaToId(int id){
        SqlRowSet rows = jdbcTemplate.queryForRowSet("select * from RATING where RATING_ID = ?", id);
        if (!rows.next()) {
            throw new NotFoundException("Такого рейтинга нет");
        }
        String sql = "select * from rating where RATING_ID = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
                new Mpa(rs.getInt("rating_id"), rs.getString("name"),
                        rs.getString("description")), id);
    }
}
