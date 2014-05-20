package net.nemerosa.ontrack.extension.support;

import net.nemerosa.ontrack.extension.api.Extension;
import net.nemerosa.ontrack.extension.api.ExtensionFeature;

public abstract class AbstractExtension implements Extension {

    private final ExtensionFeature extensionFeature;

    public AbstractExtension(ExtensionFeature extensionFeature) {
        this.extensionFeature = extensionFeature;
    }

    @Override
    public ExtensionFeature getFeature() {
        return extensionFeature;
    }
}
