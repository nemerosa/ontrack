package net.nemerosa.ontrack.model.exceptions;

public class PropertyTypeNotFoundException extends BaseException {
    public PropertyTypeNotFoundException(String propertyTypeName) {
        super("Cannot find property type with name: %s", propertyTypeName);
    }
}
