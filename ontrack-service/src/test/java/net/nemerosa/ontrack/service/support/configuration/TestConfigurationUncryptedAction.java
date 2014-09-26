package net.nemerosa.ontrack.service.support.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.model.support.DBMigrationAction;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * This action creates an uncrypted (clear) version of a configuration. The
 * {@link ConfigurationServiceIT#encryptedConfigurationMigration()} method
 * checks that in the end, this configuration has been encrypted.
 */
@Component
public class TestConfigurationUncryptedAction implements DBMigrationAction {

    private final ObjectMapper objectMapper = ObjectMapperFactory.create();

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
    public void migrate(Connection connection) throws SQLException, JsonProcessingException {
        try (PreparedStatement ps =
                     connection.prepareStatement("INSERT INTO CONFIGURATIONS(TYPE, NAME, CONTENT) VALUES (?, ?, ?)")) {
            ps.setString(1, TestConfiguration.class.getName());
            ps.setString(2, "plain");
            ps.setString(3, objectMapper.writeValueAsString(
                            new TestConfiguration(
                                    "plain",
                                    "test",
                                    "verysecret"
                            )
                    )
            );
            ps.execute();
        }
    }

    @Override
    public String getDisplayName() {
        return "Creation of plain configuration for encryption migration test";
    }
}
