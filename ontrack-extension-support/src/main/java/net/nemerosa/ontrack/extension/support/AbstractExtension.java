package net.nemerosa.ontrack.extension.support;

import net.nemerosa.ontrack.extension.api.Extension;

public abstract class AbstractExtension implements Extension {

    private final String id;
    private final String name;
    private final String description;

    public AbstractExtension(String id, String name, String description) {
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
