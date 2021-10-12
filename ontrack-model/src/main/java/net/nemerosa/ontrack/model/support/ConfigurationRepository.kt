package net.nemerosa.ontrack.model.support;

import java.util.List;
import java.util.Optional;

public interface ConfigurationRepository {

    /**
     * Gets the list of items for this configuration class
     */
    <T extends Configuration<T>> List<T> list(Class<T> configurationClass);

    /**
     * Gets a configuration using its name
     */
    <T extends Configuration<T>> Optional<T> find(Class<T> configurationClass, String name);

    /**
     * Saves or creates a configuration
     */
    <T extends Configuration<T>> T save(T configuration);

    /**
     * Deletes a configuration
     */
    <T extends Configuration<T>> void delete(Class<T> configurationClass, String name);

}
