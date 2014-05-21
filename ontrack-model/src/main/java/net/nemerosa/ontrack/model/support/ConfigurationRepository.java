package net.nemerosa.ontrack.model.support;

import java.util.Collection;
import java.util.Optional;

public interface ConfigurationRepository {

    /**
     * Gets the list of items for this configuration class
     */
    <T extends Configuration> Collection<T> list(Class<T> configurationClass);

    /**
     * Gets a configuration using its name
     */
    <T extends Configuration> Optional<T> find(Class<T> configurationClass, String name);

    /**
     * Saves or creates a configuration
     */
    <T extends Configuration> T save(T configuration);
}
