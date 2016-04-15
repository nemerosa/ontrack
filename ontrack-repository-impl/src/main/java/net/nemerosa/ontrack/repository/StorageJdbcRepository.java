package net.nemerosa.ontrack.repository;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    @Override
    public List<String> getKeys(String store) {
        return getNamedParameterJdbcTemplate().queryForList(
                "SELECT NAME FROM STORAGE WHERE STORE = :store ORDER BY NAME",
                params("store", store),
                String.class
        );
    }

    @Override
    public Map<String, JsonNode> getData(String store) {
        Map<String, JsonNode> results = new LinkedHashMap<>();
        //noinspection RedundantCast
        getNamedParameterJdbcTemplate().query(
                "SELECT NAME, DATA FROM STORAGE WHERE STORE = :store ORDER BY NAME",
                params("store", store),
                (RowCallbackHandler) rs -> {
                    String name = rs.getString("NAME");
                    JsonNode node = readJson(rs, "DATA");
                    results.put(name, node);
                }
        );
        return results;
    }
}
