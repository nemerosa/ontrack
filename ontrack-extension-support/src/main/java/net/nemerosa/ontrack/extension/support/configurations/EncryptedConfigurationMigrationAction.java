package net.nemerosa.ontrack.extension.support.configurations;

import net.nemerosa.ontrack.model.support.DBMigrationAction;
import org.springframework.stereotype.Component;

import java.sql.Connection;

/**
 * Migration for the configurations, so they can be encrypted
 */
@Component
public class EncryptedConfigurationMigrationAction implements DBMigrationAction {

    @Override
    public int getPatch() {
        return 3;
    }

    @Override
    public void migrate(Connection connection) {
        // FIXME Method net.nemerosa.ontrack.extension.support.configurations.EncryptedConfigurationMigrationAction.migrate
    }

    @Override
    public String getDisplayName() {
        return "Configuration encryption";
    }
}
