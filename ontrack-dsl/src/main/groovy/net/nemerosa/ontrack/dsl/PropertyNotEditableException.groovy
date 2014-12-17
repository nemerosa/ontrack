package net.nemerosa.ontrack.dsl

class PropertyNotEditableException extends DSLException {
    def PropertyNotEditableException(String type) {
        super("Property ${type} is not editable.")
    }
}
