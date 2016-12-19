package net.nemerosa.ontrack.extension.stash.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.nemerosa.ontrack.extension.stash.model.StashConfiguration;
import net.nemerosa.ontrack.extension.stash.property.StashProjectConfigurationPropertyType;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.model.support.DBMigrationAction;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Migration task for #473, for having the issue configuration at project level, not global configuration level
 */
@Component
public class BitBucketConfigurationMigrationAction implements DBMigrationAction {

    private final ObjectMapper objectMapper = ObjectMapperFactory.create();

    @Override
    public int getPatch() {
        return 35;
    }

    @Override
    public void migrate(Connection connection) throws Exception {
        // For all Git configurations
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM CONFIGURATIONS WHERE TYPE = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
            ps.setString(1, StashConfiguration.class.getName());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Configuration as JSON
                    String json = rs.getString("CONTENT");
                    // Parses the configuration
                    ObjectNode node = (ObjectNode) objectMapper.readTree(json);
                    // Gets the name
                    String configName = JsonUtils.get(node, "name");
                    // Gets the fields to migrate
                    int indexationInterval = JsonUtils.getInt(node, "indexationInterval", 0);
                    String issueServiceIdentifier = JsonUtils.get(node, "issueServiceConfigurationIdentifier", false, "");
                    // FIXME Removes interval & issue identifier
                    // Migration of project configurations if needed
                    String sqlP = "SELECT * FROM PROPERTIES WHERE TYPE = ? AND PROJECT IS NOT NULL";
                    try (PreparedStatement psp = connection.prepareStatement(sqlP, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                        psp.setString(1, StashProjectConfigurationPropertyType.class.getName());
                        try (ResultSet rsp = psp.executeQuery()) {
                            while (rsp.next()) {
                                // Property value as JSON
                                String jsonP = rsp.getString("JSON");
                                // Parses the property value
                                ObjectNode nodeP = (ObjectNode) objectMapper.readTree(jsonP);
                                // Gets the configuration name
                                String configP = JsonUtils.get(nodeP, "configuration");
                                if (StringUtils.equals(configName, configP)) {
                                    // Replaces interval & issue service identifier
                                    nodeP.put("indexationInterval", indexationInterval);
                                    nodeP.put("issueServiceConfigurationIdentifier", issueServiceIdentifier);
                                    // Saving back
                                    rsp.updateString("JSON", objectMapper.writeValueAsString(nodeP));
                                    rsp.updateRow();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getDisplayName() {
        return "BitBucket issue service configuration";
    }
}
