package net.nemerosa.ontrack.extension.github.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration;
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationPropertyType;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.model.support.DBMigrationAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Migration of the GitHub configurations.
 * <p>
 * <ul>
 * <li>The configurations do not hold the repositories any longer.</li>
 * <li>The GitHub project configurations must have a repository</li>
 * </ul>
 */
@Component
public class GitHubEngineConfigurationMigrationAction implements DBMigrationAction {

    /**
     * Version of the database to migrate.
     */
    public static final int GITHUB_ENGINE_PATCH = 18;

    private final Logger logger = LoggerFactory.getLogger(GitHubEngineConfigurationMigrationAction.class);
    private final ObjectMapper objectMapper = ObjectMapperFactory.create();

    @Override
    public int getPatch() {
        return GITHUB_ENGINE_PATCH;
    }

    @Override
    public void migrate(Connection connection) throws Exception {
        // Migrates the configurations
        Map<String, OldConfiguration> oldConfigurations = migrateConfigurations(connection);
        // Migrates the projects
        migrateProjects(connection, oldConfigurations);
    }

    private void migrateProjects(Connection connection, Map<String, OldConfiguration> oldConfigurations) throws SQLException, IOException {
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM PROPERTIES WHERE TYPE = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
            ps.setString(1, GitHubProjectConfigurationPropertyType.class.getName());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Raw property
                    String json = rs.getString("JSON");
                    // Parsing
                    ObjectNode node = (ObjectNode) objectMapper.readTree(json);
                    // Gets the configuration name
                    String configurationName = JsonUtils.get(node, "configuration");
                    // Gets the configuration from the map
                    OldConfiguration oldConfiguration = oldConfigurations.get(configurationName);
                    if (oldConfiguration == null) {
                        throw new IllegalStateException("Could not find GitHub configuration with name " + configurationName);
                    }
                    // Completes the project configuration with the old one
                    node.put("repository", oldConfiguration.getRepository());
                    node.put("indexationInterval", oldConfiguration.getIndexationInterval());
                    // Saves the configuration back
                    rs.updateString("JSON", objectMapper.writeValueAsString(node));
                    rs.updateRow();
                }
            }
        }
    }

    private Map<String, OldConfiguration> migrateConfigurations(Connection connection) throws Exception {
        // Index of configurations
        Map<String, OldConfiguration> oldConfigurations = new HashMap<>();
        // For all GitHub configurations
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM CONFIGURATIONS WHERE TYPE = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
            ps.setString(1, "net.nemerosa.ontrack.extension.github.model.GitHubConfiguration");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("NAME");
                    logger.info("Migrating GitHub configuration: {}", name);
                    // Configuration as JSON
                    String json = rs.getString("CONTENT");
                    // Parses the configuration
                    OldConfiguration oldConfiguration = objectMapper.readValue(json, OldConfiguration.class);
                    // New configuration
                    GitHubEngineConfiguration newConfiguration = new GitHubEngineConfiguration(
                            oldConfiguration.getName(),
                            GitHubEngineConfiguration.GITHUB_COM,
                            oldConfiguration.getUser(),
                            oldConfiguration.getPassword(),
                            oldConfiguration.getOauth2Token()
                    );
                    // Updates the configuration
                    rs.updateString("TYPE", GitHubEngineConfiguration.class.getName());
                    rs.updateString("CONTENT", objectMapper.writeValueAsString(newConfiguration));
                    rs.updateRow();
                    // Indexation
                    oldConfigurations.put(oldConfiguration.getName(), oldConfiguration);
                }
            }
        }
        // OK
        return oldConfigurations;
    }

    @Override
    public String getDisplayName() {
        return "GitHub configuration engine migration";
    }

    /**
     * Representation of an old configuration
     */
    @Data
    public static class OldConfiguration {
        private final String name;
        private final String repository;
        private final String user;
        private final String password;
        private final String oauth2Token;
        private final int indexationInterval;
    }
}
