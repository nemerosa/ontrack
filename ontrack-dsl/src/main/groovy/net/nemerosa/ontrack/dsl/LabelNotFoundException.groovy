package net.nemerosa.ontrack.dsl

class LabelNotFoundException extends DSLException {
    LabelNotFoundException(String category, String name) {
        super("Cannot find label: $category:$name")
    }
}
