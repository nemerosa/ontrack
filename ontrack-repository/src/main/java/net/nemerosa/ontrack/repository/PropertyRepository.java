package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.model.structure.PropertyType;

/**
 * Repository to access the properties.
 */
public interface PropertyRepository {

    TProperty loadProperty(String typeName, ProjectEntityType entityType, ID entityId);

}
