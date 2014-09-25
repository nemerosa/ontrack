package net.nemerosa.ontrack.extension.support.configurations;

import net.nemerosa.ontrack.model.support.ConfigurationRepository;
import net.nemerosa.ontrack.model.support.DBMigrationAction;
import net.nemerosa.ontrack.security.EncryptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;

/**
 * Migration for the configurations, so they can be encrypted
 */
@Component
public class EncryptedConfigurationMigrationAction implements DBMigrationAction {

    private final Logger logger = LoggerFactory.getLogger(EncryptedConfigurationMigrationAction.class);

    private final Collection<ConfigurationService<?>> configurationServices;
    private final ConfigurationRepository configurationRepository;
    private final EncryptionService encryptionService;

    @Autowired
    protected EncryptedConfigurationMigrationAction(Collection<ConfigurationService<?>> configurationServices, ConfigurationRepository configurationRepository, EncryptionService encryptionService) {
        this.configurationServices = configurationServices;
        this.configurationRepository = configurationRepository;
        this.encryptionService = encryptionService;
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
        logger.info("Encrypting configurations for: {}", configurationService.getClass().getName());
        // Class for the configuration
        Class<T> configurationType = configurationService.getConfigurationType();
        // Gets all configurations for this type
        List<T> configurations = configurationRepository.list(configurationType);
        // Saves them again to force encryption
        for (T configuration : configurations) {
            logger.info("Encrypting configuration: {}", configuration.getDescriptor().getName());
            UserPasswordConfiguration migratedConfig = configuration.withPassword(
                    encryptionService.encrypt(
                            configuration.getPassword()
                    )
            );
            configurationRepository.save(migratedConfig);
        }
    }

    @Override
    public String getDisplayName() {
        return "Configuration encryption";
    }
}
