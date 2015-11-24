package net.nemerosa.ontrack.service.support;

import net.nemerosa.ontrack.model.extension.ExtensionFeature;

/**
 * {@link net.nemerosa.ontrack.model.extension.ExtensionFeature} used by some core components.
 */
public class CoreExtensionFeature implements ExtensionFeature {

    public static final CoreExtensionFeature INSTANCE = new CoreExtensionFeature();

    @Override
    public String getId() {
        return "core";
    }

    @Override
    public String getName() {
        return "Core";
    }

    @Override
    public String getDescription() {
        return "Core components";
    }
}
