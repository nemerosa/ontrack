package net.nemerosa.ontrack.extension.general;

import net.nemerosa.ontrack.model.structure.Describable;

public enum MessageType implements Describable {

    ERROR("Error", "Error message"),

    WARNING("Warning", "Warning message"),

    INFO("Info", "Information message");

    private final String name;
    private final String description;

    MessageType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String getId() {
        return name();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

}
