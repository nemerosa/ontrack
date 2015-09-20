package net.nemerosa.ontrack.extension.support;

import net.nemerosa.ontrack.model.extension.ExtensionFeature;

public abstract class AbstractExtensionFeature implements ExtensionFeature {

    private final String id;
    private final String name;
    private final String description;

    public AbstractExtensionFeature(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    @Override
    public String getId() {
        return id;
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
