package net.nemerosa.ontrack.model.exceptions;

import net.nemerosa.ontrack.common.BaseException;

public class PropertyTypeNotFoundException extends BaseException {
    public PropertyTypeNotFoundException(String propertyTypeName) {
        super("Cannot find property type with name: %s", propertyTypeName);
    }
}
