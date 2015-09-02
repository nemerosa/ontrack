package net.nemerosa.ontrack.service.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.nemerosa.ontrack.extension.support.ConfigurationService;
import net.nemerosa.ontrack.model.support.UserPasswordConfiguration;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.model.security.EncryptionService;
import net.nemerosa.ontrack.model.support.DBMigrationAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Migration for the configurations, so they can be encrypted
 */
@Component
public class EncryptedConfigurationMigrationAction implements DBMigrationAction {

    private final Logger logger = LoggerFactory.getLogger(EncryptedConfigurationMigrationAction.class);

    private final ObjectMapper objectMapper = ObjectMapperFactory.create();

    private final ApplicationContext applicationContext;
    private final EncryptionService encryptionService;

    @Autowired
    protected EncryptedConfigurationMigrationAction(
            ApplicationContext applicationContext,
            EncryptionService encryptionService) {
        this.applicationContext = applicationContext;
        this.encryptionService = encryptionService;
    }

    @Override
    public int getPatch() {
        return 3;
    }

    @Override
    public void migrate(Connection connection) throws SQLException, IOException {
        // For all configuration services
        for (ConfigurationService<?> configurationService : applicationContext.getBeansOfType(ConfigurationService.class).values()) {
            migrate(configurationService, connection);
        }
    }

    private <T extends UserPasswordConfiguration> void migrate(ConfigurationService<T> configurationService, Connection connection) throws SQLException, IOException {
        logger.info("Encrypting configurations for: {}", configurationService.getClass().getName());
        // Class for the configuration
        Class<T> configurationType = configurationService.getConfigurationType();
        // Gets all configurations for this type
        /**
         * We cannot use the repository here since it is using its own connection through the datasource.
         */
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM CONFIGURATIONS WHERE TYPE = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
            ps.setString(1, configurationType.getName());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    logger.info("Encrypting configuration: {}", name);
                    // Configuration as JSON
                    String json = rs.getString("content");
                    // Parses the configuration
                    T data = objectMapper.readValue(json, configurationType);
                    // Encrypts the configuration
                    UserPasswordConfiguration encryptedData = data.withPassword(encryptionService.encrypt(data.getPassword()));
                    // Saves the data back
                    rs.updateString("content", objectMapper.writeValueAsString(encryptedData));
                    rs.updateRow();
                }
            }
        }
    }

    @Override
    public String getDisplayName() {
        return "Configuration encryption";
    }
}
