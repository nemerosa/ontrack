package net.nemerosa.ontrack.extension.api;

import net.nemerosa.ontrack.model.extension.Extension;

import java.util.Collection;

public interface ExtensionManager {

    <T extends Extension> Collection<T> getExtensions(Class<T> extensionType);

}
