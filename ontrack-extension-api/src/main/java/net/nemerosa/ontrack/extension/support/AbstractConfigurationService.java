package net.nemerosa.ontrack.extension.support;

import net.nemerosa.ontrack.extension.support.ConfigurationNotFoundException;
import net.nemerosa.ontrack.extension.support.ConfigurationService;
import net.nemerosa.ontrack.extension.support.UserPasswordConfiguration;
import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.support.Configuration;
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor;
import net.nemerosa.ontrack.model.support.ConfigurationRepository;
import net.nemerosa.ontrack.model.security.EncryptionService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractConfigurationService<T extends UserPasswordConfiguration<T>> implements ConfigurationService<T> {

    private final Class<T> configurationClass;
    private final ConfigurationRepository configurationRepository;
    private final SecurityService securityService;
    private final EncryptionService encryptionService;

    public AbstractConfigurationService(Class<T> configurationClass, ConfigurationRepository configurationRepository, SecurityService securityService, EncryptionService encryptionService) {
        this.configurationClass = configurationClass;
        this.configurationRepository = configurationRepository;
        this.securityService = securityService;
        this.encryptionService = encryptionService;
    }

    /**
     * Checks the accesses by checking the {@link net.nemerosa.ontrack.model.security.GlobalSettings} function.
     */
    protected void checkAccess() {
        securityService.checkGlobalFunction(GlobalSettings.class);
    }

    @Override
    public List<T> getConfigurations() {
        return configurationRepository.list(configurationClass).stream()
                .map(this::decrypt)
                .collect(Collectors.toList());
    }

    @Override
    public List<ConfigurationDescriptor> getConfigurationDescriptors() {
        return securityService.runAsAdmin(
                () -> getConfigurations().stream()
                        .map(Configuration::getDescriptor)
                        .collect(Collectors.toList())
        ).get();
    }

    @Override
    public T newConfiguration(T configuration) {
        checkAccess();
        validateConfiguration(configuration);
        configurationRepository.save(encrypt(configuration));
        return configuration;
    }

    @Override
    public T getConfiguration(String name) {
        return configurationRepository
                .find(configurationClass, name)
                .map(this::decrypt)
                .orElseThrow(() -> new ConfigurationNotFoundException(name));
    }

    @Override
    public Optional<T> getOptionalConfiguration(String name) {
        return configurationRepository.find(configurationClass, name).map(this::decrypt);
    }

    @Override
    public void deleteConfiguration(String name) {
        checkAccess();
        configurationRepository.delete(configurationClass, name);
    }

    @Override
    public void updateConfiguration(String name, T configuration) {
        checkAccess();
        validateConfiguration(configuration);
        Validate.isTrue(StringUtils.equals(name, configuration.getName()), "Configuration name must match");
        T configToSave;
        if (StringUtils.isBlank(configuration.getPassword())) {
            T oldConfig = getConfiguration(name);
            if (StringUtils.equals(oldConfig.getUser(), configuration.getUser())) {
                configToSave = configuration.withPassword(oldConfig.getPassword());
            } else {
                configToSave = configuration;
            }
        } else {
            configToSave = configuration;
        }
        configurationRepository.save(encrypt(configToSave));
    }

    /**
     * Extra validation
     */
    protected void validateConfiguration(T configuration) {
    }

    @Override
    public T replaceConfiguration(T configuration, Function<String, String> replacementFunction) throws ConfigurationNotFoundException {
        // Tries to replace the configuration name
        String sourceConfigurationName = configuration.getName();
        String targetConfigurationName = replacementFunction.apply(sourceConfigurationName);
        // If not different, we can use the same configuration
        if (StringUtils.equals(sourceConfigurationName, targetConfigurationName)) {
            return configuration;
        }
        // If different, we need to create a new configuration
        else if (!securityService.isGlobalFunctionGranted(GlobalSettings.class)) {
            throw new ConfigurationNotFoundException(targetConfigurationName);
        } else {
            // Clones the configuration
            T targetConfiguration = configuration.clone(targetConfigurationName, replacementFunction);
            // Saves the configuration
            return newConfiguration(targetConfiguration);
        }
    }

    @Override
    public Class<T> getConfigurationType() {
        return configurationClass;
    }

    protected T encrypt(T config) {
        return config.withPassword(
                encryptionService.encrypt(
                        config.getPassword()
                )
        );
    }

    protected T decrypt(T config) {
        return config.withPassword(
                encryptionService.decrypt(
                        config.getPassword()
                )
        );
    }

}
