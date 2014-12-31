package net.nemerosa.ontrack.extension.git.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.nemerosa.ontrack.extension.git.model.BasicGitConfiguration;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.model.support.DBMigrationAction;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Set;

/**
 * Migration task for #204, for cleaning up the Git configurations.
 */
@Component
public class GitConfigurationMigrationAction11 implements DBMigrationAction {

    private final ObjectMapper objectMapper = ObjectMapperFactory.create();

    private final Set<String> validFields = Sets.newHashSet(
            "fileAtCommitLink",
            "commitLink",
            "issueServiceConfigurationIdentifier",
            "remote",
            "indexationInterval",
            "name",
            "password",
            "user"
    );

    @Override
    public int getPatch() {
        return 11;
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
                    // Gets all fields of the node
                    List<String> fieldNames = Lists.newArrayList(node.fieldNames());
                    // Removes any field that does not belong to the BasicGitConfiguration class
                    fieldNames.stream()
                            .filter(fieldName -> !validFields.contains(fieldName))
                            .forEach(node::remove);
                    // Updating
                    rs.updateString("CONTENT", objectMapper.writeValueAsString(node));
                    rs.updateRow();
                }
            }
        }
    }

    @Override
    public String getDisplayName() {
        return "Git configuration migration (patch 11)";
    }
}
