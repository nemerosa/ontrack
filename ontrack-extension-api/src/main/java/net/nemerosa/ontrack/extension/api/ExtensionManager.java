package net.nemerosa.ontrack.extension.api;

import net.nemerosa.ontrack.model.extension.Extension;
import net.nemerosa.ontrack.model.extension.ExtensionFeature;
import net.nemerosa.ontrack.model.extension.ExtensionList;

import java.util.Collection;
import java.util.List;

public interface ExtensionManager {

    <T extends Extension> Collection<T> getExtensions(Class<T> extensionType);

    /**
     * Gets the list of all accessible extensions.
     */
    List<ExtensionFeature> getExtensionFeatures();

    /**
     * Gets the list of extensions and the associated dependency graph
     */
    ExtensionList getExtensionList();
}
