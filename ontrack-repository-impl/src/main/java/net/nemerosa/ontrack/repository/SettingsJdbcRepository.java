package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import net.nemerosa.ontrack.security.EncryptionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.function.Function;

@Repository
public class SettingsJdbcRepository extends AbstractJdbcRepository implements SettingsRepository {

    private final EncryptionService encryptionService;

    @Autowired
    public SettingsJdbcRepository(DataSource dataSource, EncryptionService encryptionService) {
        super(dataSource);
        this.encryptionService = encryptionService;
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

    @Override
    public String getString(Class<?> category, String name, String defaultValue) {
        return getValue(category, name, Function.identity(), defaultValue);
    }

    @Override
    public void setString(Class<?> category, String name, String value) {
        setValue(category, name, value);
    }

    @Override
    public String getPassword(Class<?> category, String name, String defaultValue) {
        return getValue(
                category,
                name,
                encryptionService::decrypt,
                defaultValue
        );
    }

    @Override
    public void setPassword(Class<?> category, String name, String plain, boolean dontSaveIfBlank) {
        if (!StringUtils.isBlank(plain) || !dontSaveIfBlank) {
            setValue(category, name, encryptionService.encrypt(plain));
        }
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
