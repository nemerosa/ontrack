package net.nemerosa.ontrack.extension.api;

import net.nemerosa.ontrack.model.extension.Extension;
import net.nemerosa.ontrack.model.extension.ExtensionFeature;

import java.util.Collection;
import java.util.List;

public interface ExtensionManager {

    <T extends Extension> Collection<T> getExtensions(Class<T> extensionType);

    /**
     * Gets the list of all accessible extensions.
     */
    List<ExtensionFeature> getExtensionFeatures();

}
