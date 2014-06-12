package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.function.Function;

@Repository
public class SettingsJdbcRepository extends AbstractJdbcRepository implements SettingsRepository {

    @Autowired
    public SettingsJdbcRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean getBoolean(Class<?> category, String name, boolean defaultValue) {
        return getValue(
                category,
                name,
                Boolean::valueOf,
                defaultValue
        );
    }

    @Override
    public void setBoolean(Class<?> category, String name, boolean value) {
        setValue(category, name, String.valueOf(value));
    }

    protected void setValue(Class<?> category, String name, String value) {
        MapSqlParameterSource params = params("category", category.getName()).addValue("name", name);
        getNamedParameterJdbcTemplate().update(
                "DELETE FROM SETTINGS WHERE CATEGORY = :category AND NAME = :name",
                params
        );
        getNamedParameterJdbcTemplate().update(
                "INSERT INTO SETTINGS (CATEGORY, NAME, VALUE) VALUES (:category, :name, :value)",
                params.addValue("value", value)
        );
    }

    protected <T> T getValue(Class<?> category, String name, Function<String, T> converter, T defaultValue) {
        String value = getFirstItem(
                "SELECT VALUE FROM SETTINGS WHERE CATEGORY = :category AND NAME = :name",
                params("category", category.getName()).addValue("name", name),
                String.class
        );
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        } else {
            return converter.apply(value);
        }
    }
}
