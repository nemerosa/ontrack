package net.nemerosa.ontrack.extension.support;

import net.nemerosa.ontrack.model.events.EventFactory;
import net.nemerosa.ontrack.model.events.EventPostService;
import net.nemerosa.ontrack.model.security.EncryptionService;
import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.support.*;
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
    private final EventPostService eventPostService;
    private final EventFactory eventFactory;

    public AbstractConfigurationService(Class<T> configurationClass, ConfigurationRepository configurationRepository, SecurityService securityService, EncryptionService encryptionService, EventPostService eventPostService, EventFactory eventFactory) {
        this.configurationClass = configurationClass;
        this.configurationRepository = configurationRepository;
        this.securityService = securityService;
        this.encryptionService = encryptionService;
        this.eventPostService = eventPostService;
        this.eventFactory = eventFactory;
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
        validate(configuration);
        configurationRepository.save(encrypt(configuration));
        eventPostService.post(eventFactory.newConfiguration(configuration));
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
        T configuration = configurationRepository.find(configurationClass, name).get();
        // Notifies of the deletion BEFORE the actual deletion, giving a change to the listeners to list access the configuration
        eventPostService.post(eventFactory.deleteConfiguration(configuration));
        // Actual deletion
        configurationRepository.delete(configurationClass, name);
    }

    @Override
    public void updateConfiguration(String name, T configuration) {
        checkAccess();
        Validate.isTrue(StringUtils.equals(name, configuration.getName()), "Configuration name must match");
        T configToSave = injectCredentials(configuration);
        validate(configToSave);
        configurationRepository.save(encrypt(configToSave));
        eventPostService.post(eventFactory.updateConfiguration(configuration));
    }

    /**
     * Adjust a configuration so that it contains a password if
     * 1) the password is empty
     * 2) the configuration already exists
     * 3) the user name is the same
     */
    protected T injectCredentials(T configuration) {
        T target;
        if (StringUtils.isBlank(configuration.getPassword())) {
            T oldConfig = getConfiguration(configuration.getName());
            if (StringUtils.equals(oldConfig.getUser(), configuration.getUser())) {
                target = configuration.withPassword(oldConfig.getPassword());
            } else {
                target = configuration;
            }
        } else {
            target = configuration;
        }
        return target;
    }

    @Override
    public ConnectionResult test(T configuration) {
        return validate(injectCredentials(configuration));
    }

    /**
     * Validates a configuration
     */
    protected abstract ConnectionResult validate(T configuration);


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
