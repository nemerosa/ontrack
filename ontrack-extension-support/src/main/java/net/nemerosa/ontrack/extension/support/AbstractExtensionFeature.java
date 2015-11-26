package net.nemerosa.ontrack.extension.support;

import net.nemerosa.ontrack.model.extension.ExtensionFeature;
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions;

public abstract class AbstractExtensionFeature implements ExtensionFeature {

    private final String id;
    private final String name;
    private final String description;
    private final ExtensionFeatureOptions options;

    public AbstractExtensionFeature(String id, String name, String description) {
        this(id, name, description, ExtensionFeatureOptions.DEFAULT);
    }

    public AbstractExtensionFeature(String id, String name, String description, ExtensionFeatureOptions options) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.options = options;
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

    @Override
    public ExtensionFeatureOptions getOptions() {
        return options;
    }
}
