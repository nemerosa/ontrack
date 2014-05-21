package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.support.Configuration;
import net.nemerosa.ontrack.model.support.ConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Collection;

@Repository
public class ConfigurationJdbcRepository extends AbstractJdbcRepository implements ConfigurationRepository {

    @Autowired
    public ConfigurationJdbcRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public <T extends Configuration> Collection<T> list(Class<T> configurationClass) {
        return getNamedParameterJdbcTemplate().query(
                "SELECT * FROM CONFIGURATIONS WHERE TYPE = :type ORDER BY NAME",
                params("type", configurationClass.getName()),
                (rs, rowNum) -> readJson(configurationClass, rs, "content")
        );
    }
}
