package net.nemerosa.ontrack.extension.support.configurations;

import net.nemerosa.ontrack.model.support.ConfigurationDescriptor;

import java.util.List;

public interface ConfigurationService<T extends UserPasswordConfiguration> {

    List<T> getConfigurations();

    List<ConfigurationDescriptor> getConfigurationDescriptors();

    T newConfiguration(T configuration);

    T getConfiguration(String name);

    void deleteConfiguration(String name);

    /**
     * Gets the former password if new password is blank for the same user. For a new user,
     * a blank password can be accepted.
     */
    void updateConfiguration(String name, T configuration);
}
