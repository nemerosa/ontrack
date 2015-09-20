package net.nemerosa.ontrack.extension.api;

import java.util.Collection;

public interface ExtensionManager {

    <T extends Extension> Collection<T> getExtensions(Class<T> extensionType);

}
