package net.nemerosa.ontrack.repository;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;

/**
 * Raw definition of a property value in a repository.
 */
@Data
public class TProperty {

    private final String propertyTypeName;
    private final ProjectEntityType entityType;
    private final ID entityId;
    private final String searchKey;
    private final JsonNode json;

}
