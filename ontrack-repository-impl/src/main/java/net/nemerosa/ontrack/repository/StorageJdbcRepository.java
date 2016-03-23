package net.nemerosa.ontrack.repository;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Optional;

@Repository
public class StorageJdbcRepository extends AbstractJdbcRepository implements StorageRepository {

    @Autowired
    public StorageJdbcRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void storeJson(String store, String key, JsonNode node) {
        MapSqlParameterSource params = params("store", store).addValue("key", key);
        // Deleting first
        getNamedParameterJdbcTemplate().update(
                "DELETE FROM STORAGE WHERE STORE = :store AND NAME = :key",
                params
        );
        // Inserting if not null
        if (node != null) {
            getNamedParameterJdbcTemplate().update(
                    "INSERT INTO STORAGE(STORE, NAME, DATA) VALUES (:store, :key, :data)",
                    params.addValue("data", writeJson(node))
            );
        }
    }

    @Override
    public Optional<JsonNode> retrieveJson(String store, String key) {
        return getOptional(
                "SELECT DATA FROM STORAGE WHERE STORE = :store AND NAME = :key",
                params("store", store).addValue("key", key),
                (rs, rowNum) -> {
                    return readJson(rs, "DATA");
                }
        );
    }
}
