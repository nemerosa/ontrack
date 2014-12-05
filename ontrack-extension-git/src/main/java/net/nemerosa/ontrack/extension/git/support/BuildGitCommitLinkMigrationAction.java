package net.nemerosa.ontrack.extension.git.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationProperty;
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.model.structure.ServiceConfiguration;
import net.nemerosa.ontrack.model.support.DBMigrationAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Migration task for #163, for transforming the Git branch configurations tag patterns into
 * corresponding {@link net.nemerosa.ontrack.extension.git.model.BuildGitCommitLink} configurations.
 */
@Component
public class BuildGitCommitLinkMigrationAction implements DBMigrationAction {


    private final Logger logger = LoggerFactory.getLogger(BuildGitCommitLinkMigrationAction.class);
    private final ObjectMapper objectMapper = ObjectMapperFactory.create();

    @Override
    public int getPatch() {
        return 7;
    }

    @Override
    public void migrate(Connection connection) throws Exception {
        migrateGitConfigurations(connection);
        migrateBranchConfigurations(connection);
    }

    private void migrateGitConfigurations(Connection connection) throws SQLException, IOException {
        // For all Git configurations
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM CONFIGURATIONS WHERE TYPE = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
            ps.setString(1, "net.nemerosa.ontrack.extension.git.model.GitConfiguration");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("NAME");
                    logger.info("Migrating Git configuration: {}", name);
                    // Configuration as JSON
                    String json = rs.getString("CONTENT");
                    // Parses the configuration
                    ObjectNode node = (ObjectNode) objectMapper.readTree(json);
                    // Removes any `tagPattern`
                    node.remove("tagPattern");
                    // Saves the data back
                    rs.updateString("CONTENT", objectMapper.writeValueAsString(node));
                    rs.updateRow();
                }
            }
        }
    }

    private void migrateBranchConfigurations(Connection connection) throws SQLException, IOException {
        // For all branches...
        try (PreparedStatement psBranches = connection.prepareStatement("SELECT ID FROM BRANCHES")) {
            try (ResultSet rsBranches = psBranches.executeQuery()) {
                while (rsBranches.next()) {
                    int branchId = rsBranches.getInt("ID");
                    // For all the Git branch configurations
                    try (PreparedStatement psProperty = connection.prepareStatement("SELECT * FROM PROPERTIES WHERE TYPE = ? AND BRANCH = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                        psProperty.setString(1, GitBranchConfigurationPropertyType.class.getName());
                        psProperty.setInt(2, branchId);
                        try (ResultSet rsProperty = psProperty.executeQuery()) {
                            while (rsProperty.next()) {
                                logger.info("Migrating Branch Git configuration: {}", branchId);
                                // Configuration as JSON
                                String json = rsProperty.getString("JSON");
                                // Parses the configuration
                                JsonNode node = objectMapper.readTree(json);
                                // Gets any existing tag pattern
                                String tagPattern = JsonUtils.get(node, "tagPattern", "*");
                                // Creates the commit link
                                ServiceConfiguration buildCommitLink;
                                if ("*".equals(tagPattern)) {
                                    buildCommitLink = TagBuildNameGitCommitLink.DEFAULT.toServiceConfiguration();
                                } else {
                                    buildCommitLink = new ServiceConfiguration(
                                            "tagPattern",
                                            JsonUtils.object()
                                                    .with("pattern", tagPattern)
                                                    .end()
                                    );
                                }
                                // Creates the new property
                                GitBranchConfigurationProperty property = new GitBranchConfigurationProperty(
                                        JsonUtils.get(node, "branch"),
                                        buildCommitLink,
                                        JsonUtils.getBoolean(node, "override"),
                                        JsonUtils.getInt(node, "buildTagInterval")
                                );
                                // Saves the data back
                                rsProperty.updateString("json", objectMapper.writeValueAsString(property));
                                rsProperty.updateRow();
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getDisplayName() {
        return "Git branch tag pattern migration to build commit links";
    }
}
