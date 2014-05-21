package net.nemerosa.ontrack.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.model.exceptions.JsonParsingException;
import net.nemerosa.ontrack.model.exceptions.JsonWritingException;
import net.nemerosa.ontrack.model.structure.ID;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public abstract class AbstractJdbcRepository extends NamedParameterJdbcDaoSupport {

    private final ObjectMapper objectMapper = ObjectMapperFactory.create();

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

    protected ID id(ResultSet rs) throws SQLException {
        return id(rs, "id");
    }

    protected ID id(ResultSet rs, String idColumn) throws SQLException {
        return id(rs.getInt(idColumn));
    }

    protected ID id(int id) {
        return ID.of(id);
    }

    protected String dateTimeForDB(LocalDateTime time) {
        if (time == null) {
            return null;
        } else {
            return time.format(DateTimeFormatter.ISO_DATE_TIME);
        }
    }

    protected <E extends Enum<E>> E getEnum(Class<E> enumClass, ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        if (value == null) {
            return null;
        } else {
            return Enum.valueOf(enumClass, value);
        }
    }

    protected String writeJson(Object any) {
        try {
            return objectMapper.writeValueAsString(any);
        } catch (JsonProcessingException e) {
            throw new JsonWritingException(e);
        }
    }

    protected <T> T readJson(Class<T> type, ResultSet rs, String column) throws SQLException {
        String json = rs.getString(column);
        try {
            if (StringUtils.isBlank(json)) {
                return null;
            } else {
                return objectMapper.readValue(json, type);
            }
        } catch (IOException ex) {
            throw new JsonParsingException(json, ex);
        }
    }

}
