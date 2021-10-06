package net.nemerosa.ontrack.model.support;

/**
 * Type of {@linkplain net.nemerosa.ontrack.model.structure.PropertyType property} which gives access to
 * a {@link Configuration configuration}.
 *
 * @param <T> Type of the configuration
 */
public interface ConfigurationProperty<T extends Configuration<T>> {

    /**
     * Access to the configuration.
     */
    T getConfiguration();

}
