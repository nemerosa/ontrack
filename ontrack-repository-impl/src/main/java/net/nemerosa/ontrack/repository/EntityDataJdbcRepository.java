package net.nemerosa.ontrack.repository;

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
