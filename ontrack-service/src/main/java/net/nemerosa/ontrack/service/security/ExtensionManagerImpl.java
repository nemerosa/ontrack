package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.extension.api.Extension;
import net.nemerosa.ontrack.extension.api.ExtensionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// TODO As startup service
@Service
public class ExtensionManagerImpl implements ExtensionManager {

    private final Map<String, ? extends Extension> extensions;

    @Autowired
    public ExtensionManagerImpl(Collection<Extension> extensions) {
        this.extensions = extensions.stream().collect(Collectors.toMap(
                Extension::getId,
                Function.identity()
        ));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Extension> Collection<T> getExtensions(Class<T> extensionType) {
        List<Extension> collection = extensions.values().stream().filter(extensionType::isInstance).collect(Collectors.toList());
        return (Collection<T>) collection;
    }

}
