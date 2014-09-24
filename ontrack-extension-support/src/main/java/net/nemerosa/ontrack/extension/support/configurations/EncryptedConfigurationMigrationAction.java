package net.nemerosa.ontrack.extension.support.configurations;

import net.nemerosa.ontrack.model.support.ConfigurationRepository;
import net.nemerosa.ontrack.model.support.DBMigrationAction;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;

/**
 * Migration for the configurations, so they can be encrypted
 */
public class EncryptedConfigurationMigrationAction implements DBMigrationAction {

    private final Collection<ConfigurationService<?>> configurationServices;
    private final ConfigurationRepository configurationRepository;

    protected EncryptedConfigurationMigrationAction(Collection<ConfigurationService<?>> configurationServices, ConfigurationRepository configurationRepository) {
        this.configurationServices = configurationServices;
        this.configurationRepository = configurationRepository;
    }

    @Override
    public int getPatch() {
        return 3;
    }

    @Override
    public void migrate(Connection connection) {
        // For all configuration services
        for (ConfigurationService<?> configurationService : configurationServices) {
            migrate(configurationService);
        }
    }

    private <T extends UserPasswordConfiguration> void migrate(ConfigurationService<T> configurationService) {
        // Class for the configuration
        Class<T> configurationType = configurationService.getConfigurationType();
        // Gets all configurations for this type
        List<T> configurations = configurationRepository.list(configurationType);
        // Saves them again to force encryption
        for (T configuration : configurations) {
            configurationService.updateConfiguration(configuration.getName(), configuration);
        }
    }

    @Override
    public String getDisplayName() {
        return "Configuration encryption";
    }
}
