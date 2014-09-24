package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.extension.api.AvailableExtension;
import net.nemerosa.ontrack.extension.api.Extension;
import net.nemerosa.ontrack.extension.api.ExtensionFeature;
import net.nemerosa.ontrack.extension.api.ExtensionManager;
import net.nemerosa.ontrack.model.support.StartupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExtensionManagerImpl implements ExtensionManager, StartupService {

    private final Logger logger = LoggerFactory.getLogger(ExtensionManager.class);

    private final ApplicationContext applicationContext;
    private Collection<? extends Extension> extensions;
    private Collection<? extends ExtensionFeature> extensionFeatures;

    @Autowired
    public ExtensionManagerImpl(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public int startupOrder() {
        return 2;
    }

    /**
     * Startup: loads the extensions & features from the application context.
     * <p/>
     * This cannot be done at construction time because of dependency cycle between
     * some extensions that need access to the extension manager.
     */
    @Override
    public void start() {
        logger.info("[extensions] Loading the extensions");
        extensions = applicationContext.getBeansOfType(Extension.class).values();
        logger.info("[extensions] Number of loaded extensions: {}", extensions.size());
        extensionFeatures = applicationContext.getBeansOfType(ExtensionFeature.class).values();
        logger.info("[extensions] Extension features:");
        for (ExtensionFeature feature : extensionFeatures) {
            logger.info("[extensions] * {} [{}]", feature.getName(), feature.getId());
        }
        // TODO Adds the loaded features into the management access points
    }

    @Override
    public <T extends Extension> Collection<T> getExtensions(Class<T> extensionType) {
        // Filters the extensions
        List<Extension> collection = extensions.stream()
                .filter(extensionType::isInstance)
                .filter(this::isExtensionEnabled)
                .collect(Collectors.<Extension>toList());
        //noinspection unchecked
        return (Collection<T>) collection;
    }

    @Override
    public <T extends Extension> Collection<AvailableExtension<T>> getAllExtensions(Class<T> extensionType) {
        // Filters the extensions
        @SuppressWarnings("unchecked")
        List<AvailableExtension<T>> collection = extensions.stream()
                .filter(extensionType::isInstance)
                .map(x -> new AvailableExtension<T>(
                        (T) x,
                        isExtensionEnabled(x)
                ))
                .collect(Collectors.toList());
        return collection;
    }

    @Override
    public boolean isExtensionEnabled(Extension x) {
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
