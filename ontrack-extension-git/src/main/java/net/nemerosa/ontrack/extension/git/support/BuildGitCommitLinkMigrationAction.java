package net.nemerosa.ontrack.extension.git.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationProperty;
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.model.structure.ServiceConfiguration;
import net.nemerosa.ontrack.model.support.DBMigrationAction;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Migration task for #163, for transforming the Git branch configurations tag patterns into
 * corresponding {@link net.nemerosa.ontrack.extension.git.model.BuildGitCommitLink} configurations.
 */
@Component
public class BuildGitCommitLinkMigrationAction implements DBMigrationAction {

    private final ObjectMapper objectMapper = ObjectMapperFactory.create();

    @Override
    public int getPatch() {
        return 7;
    }

    @Override
    public void migrate(Connection connection) throws Exception {
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
                                // Configuration as JSON
                                String json = rsProperty.getString("JSON");
                                // Parses the configuration
                                JsonNode node = objectMapper.readTree(json);
                                // Gets any existing tag pattern
                                String tagPattern = JsonUtils.get(node, "tagPattern", "*");
                                // Parses into the new configuration
                                GitBranchConfigurationProperty property =
                                        objectMapper.treeToValue(node, GitBranchConfigurationProperty.class);
                                // Creates the commit link
                                if ("*".equals(tagPattern)) {
                                    property = property.withBuildCommitLink(
                                            TagBuildNameGitCommitLink.DEFAULT.toServiceConfiguration()
                                    );
                                } else {
                                    property = property.withBuildCommitLink(
                                            new ServiceConfiguration(
                                                    "tagPattern",
                                                    JsonUtils.object()
                                                            .with("tagPattern", tagPattern)
                                                            .end()
                                            )
                                    );
                                }
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
