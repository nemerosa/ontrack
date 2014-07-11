package net.nemerosa.ontrack.repository;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Optional;

@Repository
public class PreferencesJdbcRepository extends AbstractJdbcRepository implements PreferencesRepository {

    @Autowired
    public PreferencesJdbcRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<JsonNode> find(int accountId, String type) {
        return Optional.ofNullable(
                getFirstItem(
                        "SELECT CONTENT FROM PREFERENCES WHERE ACCOUNTID = :accountId AND TYPE = :type",
                        params("accountId", accountId).addValue("type", type),
                        String.class
                )
        ).map(this::readJson);
    }

    @Override
    public void store(int accountId, String type, JsonNode data) {
        NamedParameterJdbcTemplate t = getNamedParameterJdbcTemplate();
        MapSqlParameterSource params = params("accountId", accountId).addValue("type", type);
        t.update(
                "DELETE FROM PREFERENCES WHERE ACCOUNTID = :accountId AND TYPE = :type",
                params
        );
        t.update(
                "INSERT INTO PREFERENCES(ACCOUNTID, TYPE, CONTENT) VALUES (:accountId, :type, :content)",
                params.addValue("content", writeJson(data))
        );
    }
}
