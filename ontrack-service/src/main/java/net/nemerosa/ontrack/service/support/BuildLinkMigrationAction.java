package net.nemerosa.ontrack.service.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.model.support.DBMigrationAction;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class BuildLinkMigrationAction implements DBMigrationAction {

    private final ObjectMapper objectMapper = ObjectMapperFactory.create();

    @Override
    public int getPatch() {
        return 31;
    }

    @Override
    public void migrate(Connection connection) throws Exception {
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM PROPERTIES WHERE TYPE = 'net.nemerosa.ontrack.extension.general.BuildLinkPropertyType'", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Configuration as JSON
                    String json = rs.getString("JSON");
                    // Build source
                    int buildId = rs.getInt("BUILD");
                    // Parses the configuration as JSON
                    ObjectNode node = (ObjectNode) objectMapper.readTree(json);
                    // Migrates the property
                    migrateBuildLinks(connection, buildId, node);
                    // Deleting the row
                    rs.deleteRow();
                }
            }
        }
    }

    private void migrateBuildLinks(Connection connection, int buildId, ObjectNode node) throws SQLException {
        // Gets the list of links
        JsonNode links = node.get("links");
        for (JsonNode link : links) {
            migrateBuildLink(connection, buildId, link);
        }
    }

    private void migrateBuildLink(Connection connection, int buildId, JsonNode link) throws SQLException {
        String project = JsonUtils.get(link, "project");
        String build = JsonUtils.get(link, "build");
        // Gets the target build
        Integer targetBuildId;
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT B.ID FROM BUILDS B " +
                        "INNER JOIN BRANCHES BR ON BR.ID = B.BRANCHID " +
                        "INNER JOIN PROJECTS P ON P.ID = BR.PROJECTID " +
                        "WHERE B.NAME = ? AND P.NAME = ?")) {
            ps.setString(1, build);
            ps.setString(2, project);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    targetBuildId = rs.getInt(1);
                } else {
                    targetBuildId = null;
                }
            }
        }
        // Creates an entry in the build_links table
        if (targetBuildId != null) {
            try (PreparedStatement ps = connection.prepareStatement("INSERT INTO BUILD_LINKS (BUILDID, TARGETBUILDID) VALUES (?, ?)")) {
                ps.setInt(1, buildId);
                ps.setInt(2, targetBuildId);
                try {
                    ps.executeUpdate();
                } catch (SQLException ignored) {
                    // Ignoring exceptions (likely to be duplications)
                }
            }
        }
    }

    @Override
    public String getDisplayName() {
        return "Build links as a table";
    }
}
