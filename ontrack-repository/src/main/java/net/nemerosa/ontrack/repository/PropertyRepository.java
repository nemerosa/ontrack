package net.nemerosa.ontrack.repository;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;

/**
 * Repository to access the properties.
 */
public interface PropertyRepository {

    TProperty loadProperty(String typeName, ProjectEntityType entityType, ID entityId);

    void saveProperty(String typeName, ProjectEntityType entityType, ID entityId, JsonNode data);

}
