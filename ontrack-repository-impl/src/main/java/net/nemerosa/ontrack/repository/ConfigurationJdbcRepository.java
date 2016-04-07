package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.support.Configuration;
import net.nemerosa.ontrack.model.support.ConfigurationRepository;
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
public class ConfigurationJdbcRepository extends AbstractJdbcRepository implements ConfigurationRepository {

    @Autowired
    public ConfigurationJdbcRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public <T extends Configuration<T>> List<T> list(Class<T> configurationClass) {
        return getNamedParameterJdbcTemplate().query(
                "SELECT * FROM CONFIGURATIONS WHERE TYPE = :type ORDER BY NAME",
                params("type", configurationClass.getName()),
                (rs, rowNum) -> readJson(configurationClass, rs, "content")
        );
    }

    @Override
    public <T extends Configuration<T>> Optional<T> find(Class<T> configurationClass, String name) {
        return Optional.ofNullable(
                getFirstItem(
                        "SELECT * FROM CONFIGURATIONS WHERE TYPE = :type AND NAME = :name",
                        params("type", configurationClass.getName()).addValue("name", name),
                        (rs, rowNum) -> readJson(configurationClass, rs, "content")
                )
        );
    }

    @Override
    public <T extends Configuration<T>> T save(T configuration) {
        MapSqlParameterSource params = params("type", configuration.getClass().getName()).addValue("name", configuration.getName());
        Integer id = getFirstItem(
                "SELECT ID FROM CONFIGURATIONS WHERE TYPE = :type AND NAME = :name",
                params,
                Integer.class
        );
        if (id != null) {
            // Update
            getNamedParameterJdbcTemplate().update(
                    "UPDATE CONFIGURATIONS SET CONTENT = CAST(:content AS JSONB) WHERE ID = :id",
                    params.addValue("content", writeJson(configuration)).addValue("id", id)
            );
        } else {
            // Creation
            getNamedParameterJdbcTemplate().update(
                    "INSERT INTO CONFIGURATIONS(TYPE, NAME, CONTENT) VALUES (:type, :name, CAST(:content AS JSONB))",
                    params.addValue("content", writeJson(configuration))
            );
        }
        // OK
        return configuration;
    }

    @Override
    public <T extends Configuration<T>> void delete(Class<T> configurationClass, String name) {
        getNamedParameterJdbcTemplate().update(
                "DELETE FROM CONFIGURATIONS WHERE TYPE = :type AND NAME = :name",
                params("type", configurationClass.getName()).addValue("name", name)
        );
    }
}
