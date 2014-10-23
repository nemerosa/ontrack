package net.nemerosa.ontrack.extension.api;

import java.util.Collection;

public interface ExtensionManager {

    <T extends Extension> Collection<T> getExtensions(Class<T> extensionType);

    <T extends Extension> Collection<AvailableExtension<T>> getAllExtensions(Class<T> extensionType);

    boolean isExtensionEnabled(Extension x);

    boolean isExtensionFeatureEnabled(ExtensionFeature feature);

}
