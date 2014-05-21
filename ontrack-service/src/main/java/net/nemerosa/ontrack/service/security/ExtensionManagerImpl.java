package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.extension.api.Extension;
import net.nemerosa.ontrack.extension.api.ExtensionFeature;
import net.nemerosa.ontrack.extension.api.ExtensionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

// TODO As startup service
@Service
public class ExtensionManagerImpl implements ExtensionManager {

    private final ApplicationContext applicationContext;
    private Collection<? extends Extension> extensions;
    private Collection<? extends ExtensionFeature> extensionFeatures;

    @Autowired
    public ExtensionManagerImpl(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * TODO Startup: loads the extensions & features from the application context.
     * <p/>
     * This cannot be done at construction time because of dependency cycle between
     * some extensions that need access to the extension manager.
     */
    // TODO Synchronisation can be removed when startup service is enabled
    private synchronized void startup() {
        if (extensions == null) {
            extensions = applicationContext.getBeansOfType(Extension.class).values();
        }
        if (extensionFeatures != null) {
            extensionFeatures = applicationContext.getBeansOfType(ExtensionFeature.class).values();
        }
    }

    @Override
    public <T extends Extension> Collection<T> getExtensions(Class<T> extensionType) {
        // TODO Will be loaded at startup
        startup();
        // Filters the extensions
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
