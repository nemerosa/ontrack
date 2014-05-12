package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.structure.ID;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;
import java.util.List;

public abstract class AbstractJdbcRepository extends NamedParameterJdbcDaoSupport {

    protected AbstractJdbcRepository(DataSource dataSource) {
        setDataSource(dataSource);
    }

    protected MapSqlParameterSource params(String name, Object value) {
        return new MapSqlParameterSource(name, value);
    }

    protected int dbCreate(String sql, MapSqlParameterSource params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        getNamedParameterJdbcTemplate().update(sql, params, keyHolder);
        return keyHolder.getKey().intValue();
    }

    protected <T> T getFirstItem(String sql, MapSqlParameterSource criteria, Class<T> type) {
        List<T> items = getNamedParameterJdbcTemplate().queryForList(sql, criteria, type);
        if (items.isEmpty()) {
            return null;
        } else {
            return items.get(0);
        }
    }

    protected <T> T getFirstItem(String sql, MapSqlParameterSource criteria, RowMapper<T> rowMapper) {
        List<T> items = getNamedParameterJdbcTemplate().query(sql, criteria, rowMapper);
        if (items.isEmpty()) {
            return null;
        } else {
            return items.get(0);
        }
    }

    protected ID id(int id) {
        return ID.of(id);
    }
}
