package net.nemerosa.ontrack.extension.git.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.nemerosa.ontrack.extension.git.model.BasicGitConfiguration;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.model.support.DBMigrationAction;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Migration task for #202, for cleaning up the Git configurations.
 */
@Component
public class GitConfigurationMigrationAction2 implements DBMigrationAction {

    private final ObjectMapper objectMapper = ObjectMapperFactory.create();

    @Override
    public int getPatch() {
        return 10;
    }

    @Override
    public void migrate(Connection connection) throws Exception {
        // For all Git configurations
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM CONFIGURATIONS WHERE TYPE = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
            ps.setString(1, BasicGitConfiguration.class.getName());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Configuration as JSON
                    String json = rs.getString("CONTENT");
                    // Parses the configuration
                    ObjectNode node = (ObjectNode) objectMapper.readTree(json);
                    // Removes any `branch` field
                    node.remove("branch");
                    // Updating
                    rs.updateString("CONTENT", objectMapper.writeValueAsString(node));
                    rs.updateRow();
                }
            }
        }
    }

    @Override
    public String getDisplayName() {
        return "Git configuration migration";
    }
}
