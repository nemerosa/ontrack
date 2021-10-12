package net.nemerosa.ontrack.model.support;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface ConfigurationService<T extends Configuration<T>> {

    List<T> getConfigurations();

    List<ConfigurationDescriptor> getConfigurationDescriptors();

    T newConfiguration(T configuration);

    /**
     * Gets a configuration by its name and fails if not found.
     * <p>
     * Note that the returned configuration is <i>not</i> obfuscated. It can be used internally safely
     * and will be obfuscated whenever sent to the client.
     *
     * @param name Name of the configuration to find
     * @return Found configuration
     * @throws ConfigurationNotFoundException If the configuration cannot be found
     */
    T getConfiguration(String name);

    Optional<T> getOptionalConfiguration(String name);

    void deleteConfiguration(String name);

    /**
     * Tests a configuration
     */
    ConnectionResult test(T configuration);

    /**
     * Gets the former password if new password is blank for the same user. For a new user,
     * a blank password can be accepted.
     */
    void updateConfiguration(String name, T configuration);

    /**
     * Tries to replace a configuration by another based on its name.
     * <p>
     * If the replacement function, applied on the configuration name, would
     * give the same exact name, this method returns the configuration.
     * <p>
     * If the names are different, there are two cases:
     * <ul>
     * <li>If the current user is allowed to create a new configuration,
     * the given configuration is transformed using the replacement
     * function and a new configuration is created.</li>
     * <li>If the current user is not allowed to create a configuration,
     * a {@link ConfigurationNotFoundException}
     * exception is thrown.</li>
     * </ul>
     *
     * @deprecated Will be removed in V5.
     */
    @Deprecated
    T replaceConfiguration(T configuration, Function<String, String> replacementFunction) throws ConfigurationNotFoundException;

    /**
     * Type of configuration handled by this service
     */
    Class<T> getConfigurationType();

    /**
     * Adds a configuration event listener to this service
     */
    void addConfigurationServiceListener(ConfigurationServiceListener<T> listener);
}
