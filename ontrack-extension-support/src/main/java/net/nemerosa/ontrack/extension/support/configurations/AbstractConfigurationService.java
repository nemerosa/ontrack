package net.nemerosa.ontrack.extension.support.configurations;

import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.support.Configuration;
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor;
import net.nemerosa.ontrack.model.support.ConfigurationRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractConfigurationService<T extends UserPasswordConfiguration<T>> implements ConfigurationService<T> {

    private final Class<T> configurationClass;
    private final ConfigurationRepository configurationRepository;
    private final SecurityService securityService;

    public AbstractConfigurationService(Class<T> configurationClass, ConfigurationRepository configurationRepository, SecurityService securityService) {
        this.configurationClass = configurationClass;
        this.configurationRepository = configurationRepository;
        this.securityService = securityService;
    }

    /**
     * Checks the accesses by checking the {@link net.nemerosa.ontrack.model.security.GlobalSettings} function.
     */
    protected void checkAccess() {
        securityService.checkGlobalFunction(GlobalSettings.class);
    }

    @Override
    public List<T> getConfigurations() {
        checkAccess();
        return configurationRepository.list(configurationClass);
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
        return configurationRepository.save(configuration);
    }

    @Override
    public T getConfiguration(String name) {
        checkAccess();
        return configurationRepository
                .find(configurationClass, name)
                .orElseThrow(() -> new ConfigurationNotFoundException(name));
    }

    @Override
    public void deleteConfiguration(String name) {
        checkAccess();
        configurationRepository.delete(configurationClass, name);
    }

    @Override
    public void updateConfiguration(String name, T configuration) {
        checkAccess();
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
        configurationRepository.save(configToSave);
    }
}
