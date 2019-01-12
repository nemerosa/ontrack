package net.nemerosa.ontrack.dsl;

public class LabelNotFoundException extends DSLException {
    public LabelNotFoundException(String category, String name) {
        super("Cannot find label: " + category + ":" + name);
    }
}
