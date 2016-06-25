package net.nemerosa.ontrack.repository.migration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.model.exceptions.JsonParsingException;
import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class V6__431_BuildLinkMigrationAction implements SpringJdbcMigration {

    private final ObjectMapper objectMapper = ObjectMapperFactory.create();

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        jdbcTemplate.query(
                "SELECT * FROM PROPERTIES WHERE TYPE = 'net.nemerosa.ontrack.extension.general.BuildLinkPropertyType'",
                rs -> {
                    // Configuration as JSON
                    String json = rs.getString("JSON");
                    // Build source
                    int buildId = rs.getInt("BUILD");
                    // Parses the configuration as JSON
                    ObjectNode node;
                    try {
                        node = (ObjectNode) objectMapper.readTree(json);
                    } catch (IOException e) {
                        throw new JsonParsingException(e);
                    }
                    // Migrates the property
                    migrateBuildLinks(jdbcTemplate, buildId, node);
                    // Deleting the row
                    rs.deleteRow();
                }
        );
    }

    private void migrateBuildLinks(JdbcTemplate jdbcTemplate, int buildId, ObjectNode node) throws SQLException {
        // Gets the list of links
        JsonNode links = node.get("links");
        for (JsonNode link : links) {
            migrateBuildLink(jdbcTemplate, buildId, link);
        }
    }

    private void migrateBuildLink(JdbcTemplate jdbcTemplate, int buildId, JsonNode link) throws SQLException {
        String project = JsonUtils.get(link, "project");
        String build = JsonUtils.get(link, "build");
        // Gets the target build
        List<Integer> targetBuildIds = jdbcTemplate.queryForList(
                "SELECT B.ID FROM BUILDS B " +
                        "INNER JOIN BRANCHES BR ON BR.ID = B.BRANCHID " +
                        "INNER JOIN PROJECTS P ON P.ID = BR.PROJECTID " +
                        "WHERE B.NAME = ? AND P.NAME = ?",
                Integer.class,
                build, project
        );
        // Creates an entry in the build_links table
        if (!targetBuildIds.isEmpty()) {
            jdbcTemplate.update(
                    "INSERT INTO BUILD_LINKS (BUILDID, TARGETBUILDID) VALUES (?, ?)",
                    buildId, targetBuildIds.get(0));
        }
    }

}
