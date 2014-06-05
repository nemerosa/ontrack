package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class PropertyJdbcRepository extends AbstractJdbcRepository implements PropertyRepository {

    @Autowired
    public PropertyJdbcRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public TProperty loadProperty(String typeName, ProjectEntityType entityType, ID entityId) {
        return getFirstItem(
                String.format(
                        "SELECT * FROM PROPERTIES WHERE TYPE = :type AND %s = :entityId",
                        entityType.name()
                ),
                params("type", typeName).addValue("entityId", entityId.getValue()),
                (rs, rowNum) -> toProperty(rs)
        );
    }

    private TProperty toProperty(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String searchKey = rs.getString("searchKey");
        String typeName = rs.getString("type");
        // Detects the entity
        ProjectEntityType entityType = null;
        ID entityId = null;
        for (ProjectEntityType candidate : ProjectEntityType.values()) {
            Integer candidateId = rs.getInt(candidate.name());
            if (!rs.wasNull()) {
                entityType = candidate;
                entityId = ID.of(candidateId);
            }
        }
        // Sanity check
        if (entityType == null || !ID.isDefined(entityId)) {
            throw new IllegalStateException(
                    String.format(
                            "Could not find any entity for property %s with key %s (id = %d)",
                            typeName,
                            searchKey,
                            id
                    )
            );
        }
        // OK
        return new TProperty(
                typeName,
                entityType,
                entityId,
                searchKey,
                readJson(rs, "json")
        );
    }
}
