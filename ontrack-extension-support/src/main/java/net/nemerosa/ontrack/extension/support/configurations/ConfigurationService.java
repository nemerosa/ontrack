package net.nemerosa.ontrack.extension.support.configurations;

import net.nemerosa.ontrack.model.support.ConfigurationDescriptor;

import java.util.List;
import java.util.Optional;

public interface ConfigurationService<T extends UserPasswordConfiguration> {

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
     * Gets the former password if new password is blank for the same user. For a new user,
     * a blank password can be accepted.
     */
    void updateConfiguration(String name, T configuration);

}
