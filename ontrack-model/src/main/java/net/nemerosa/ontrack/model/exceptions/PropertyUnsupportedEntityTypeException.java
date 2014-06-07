package net.nemerosa.ontrack.model.exceptions;

import net.nemerosa.ontrack.model.structure.ProjectEntityType;

public class PropertyUnsupportedEntityTypeException extends BaseException {
    public PropertyUnsupportedEntityTypeException(String typeName, ProjectEntityType entityType) {
        super("Entity type %s is not supported by property %s", entityType, typeName);
    }
}
