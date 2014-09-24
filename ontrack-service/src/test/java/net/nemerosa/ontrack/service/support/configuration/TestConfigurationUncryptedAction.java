package net.nemerosa.ontrack.service.support.configuration;

import net.nemerosa.ontrack.model.support.ConfigurationRepository;
import net.nemerosa.ontrack.model.support.DBMigrationAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;

/**
 * This action creates an uncrypted (clear) version of a configuration. The
 * {@link ConfigurationServiceIT#encryptedConfigurationMigration()} method
 * checks that in the end, this configuration has been encrypted.
 */
@Component
public class TestConfigurationUncryptedAction implements DBMigrationAction {

    private final ConfigurationRepository configurationRepository;

    @Autowired
    public TestConfigurationUncryptedAction(ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    /**
     * Makes sure we create this configuration BEFORE the migration is done.
     *
     * @see net.nemerosa.ontrack.extension.support.configurations.EncryptedConfigurationMigrationAction
     */
    @Override
    public int getPatch() {
        return 2;
    }

    @Override
    public void migrate(Connection connection) {
        configurationRepository.save(
                new TestConfiguration(
                        "plain",
                        "test",
                        "verysecret"
                )
        );
    }

    @Override
    public String getDisplayName() {
        return "Creation of plain configuration for encryption migration test";
    }
}
