package net.nemerosa.ontrack.repository;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Optional;

@Repository
public class EntityDataJdbcRepository extends AbstractJdbcRepository implements EntityDataRepository {

    @Autowired
    public EntityDataJdbcRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void store(ProjectEntity entity, String key, String value) {
        // Existing?
        Optional<Integer> existingId = getOptional(
                String.format(
                        "SELECT ID FROM ENTITY_DATA WHERE %s = :entityId AND NAME = :name",
                        entity.getProjectEntityType().name()
                ),
                params("entityId", entity.id()).addValue("name", key),
                Integer.class
        );
        if (existingId.isPresent()) {
            getNamedParameterJdbcTemplate().update(
                    "UPDATE ENTITY_DATA SET VALUE = :value WHERE ID = :id",
                    params("id", existingId.get()).addValue("value", value)
            );
        } else {
            getNamedParameterJdbcTemplate().update(
                    String.format(
                            "INSERT INTO ENTITY_DATA(%s, NAME, VALUE) VALUES (:entityId, :name, :value)",
                            entity.getProjectEntityType().name()
                    ),
                    params("entityId", entity.id())
                            .addValue("name", key)
                            .addValue("value", value)
            );
        }
    }

    @Override
    public void storeJson(ProjectEntity entity, String key, JsonNode value) {
        // Existing?
        Optional<Integer> existingId = getOptional(
                String.format(
                        "SELECT ID FROM ENTITY_DATA WHERE %s = :entityId AND NAME = :name",
                        entity.getProjectEntityType().name()
                ),
                params("entityId", entity.id()).addValue("name", key),
                Integer.class
        );
        if (existingId.isPresent()) {
            getNamedParameterJdbcTemplate().update(
                    "UPDATE ENTITY_DATA SET JSON_VALUE = CAST(:value AS JSONB) WHERE ID = :id",
                    params("id", existingId.get()).addValue("value", writeJson(value))
            );
        } else {
            getNamedParameterJdbcTemplate().update(
                    String.format(
                            "INSERT INTO ENTITY_DATA(%s, NAME, JSON_VALUE) VALUES (:entityId, :name, CAST(:value AS JSONB))",
                            entity.getProjectEntityType().name()
                    ),
                    params("entityId", entity.id())
                            .addValue("name", key)
                            .addValue("value", writeJson(value))
            );
        }
    }

    @Override
    public Optional<String> retrieve(ProjectEntity entity, String key) {
        return getOptional(
                String.format(
                        "SELECT VALUE FROM ENTITY_DATA WHERE %s = :entityId AND NAME = :name",
                        entity.getProjectEntityType().name()
                ),
                params("entityId", entity.id()).addValue("name", key),
                String.class
        );
    }

    @Override
    public Optional<JsonNode> retrieveJson(ProjectEntity entity, String key) {
        return getOptional(
                String.format(
                        "SELECT JSON_VALUE FROM ENTITY_DATA WHERE %s = :entityId AND NAME = :name",
                        entity.getProjectEntityType().name()
                ),
                params("entityId", entity.id()).addValue("name", key),
                String.class
        ).map(this::readJson);
    }

    @Override
    public void delete(ProjectEntity entity, String key) {
        getNamedParameterJdbcTemplate().update(
                String.format(
                        "DELETE FROM ENTITY_DATA WHERE %s = :entityId AND NAME = :name",
                        entity.getProjectEntityType().name()
                ),
                params("entityId", entity.id()).addValue("name", key)
        );
    }
}
