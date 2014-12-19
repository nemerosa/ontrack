package net.nemerosa.ontrack.dsl

class PropertyNotFoundException extends DSLException {
    def PropertyNotFoundException(String type) {
        super("Property ${type} is not found.")
    }
}
