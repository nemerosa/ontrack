package net.nemerosa.ontrack.dsl;

public class PropertyNotEditableException extends DSLException {
    public PropertyNotEditableException(String type) {
        super(String.format("Property %s is not editable.", type));
    }
}
