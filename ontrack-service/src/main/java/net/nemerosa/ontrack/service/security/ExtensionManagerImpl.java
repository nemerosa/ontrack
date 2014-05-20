package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.extension.api.Extension;
import net.nemerosa.ontrack.extension.api.ExtensionFeature;
import net.nemerosa.ontrack.extension.api.ExtensionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

// TODO As startup service
@Service
public class ExtensionManagerImpl implements ExtensionManager {

    private final Collection<? extends Extension> extensions;
    // TODO List of features
    private final Collection<? extends ExtensionFeature> extensionFeatures;

    @Autowired
    public ExtensionManagerImpl(Collection<? extends Extension> extensions, Collection<? extends ExtensionFeature> extensionFeatures) {
        this.extensions = extensions;
        this.extensionFeatures = extensionFeatures;
    }

    @Override
    public <T extends Extension> Collection<T> getExtensions(Class<T> extensionType) {
        List<Extension> collection = extensions.stream()
                .filter(extensionType::isInstance)
                .filter(this::isExtensionEnabled)
                .collect(Collectors.toList());
        //noinspection unchecked
        return (Collection<T>) collection;
    }

    private boolean isExtensionEnabled(Extension x) {
        return isExtensionFeatureEnabled(x.getFeature());
    }

    private boolean isExtensionFeatureEnabled(ExtensionFeature feature) {
        return isExtensionFeatureEnabled(feature.getId());
    }

    // TODO Disabling features
    // TODO Promotes to the API
    private boolean isExtensionFeatureEnabled(String id) {
        return true;
    }

}
