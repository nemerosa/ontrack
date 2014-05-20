package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.extension.api.Extension;
import net.nemerosa.ontrack.extension.api.ExtensionManager;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class ExtensionManagerImpl implements ExtensionManager {

    @Override
    public <T extends Extension> Collection<T> getExtensions(Class<T> extensionType) {
        // FIXME Method net.nemerosa.ontrack.service.security.ExtensionManagerImpl.getExtensions
        return Collections.emptyList();
    }

}
