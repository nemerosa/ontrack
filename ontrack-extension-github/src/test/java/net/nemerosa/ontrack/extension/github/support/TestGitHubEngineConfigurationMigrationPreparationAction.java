package net.nemerosa.ontrack.extension.github.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationPropertyType;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.model.support.DBMigrationAction;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Preparation of the test for the GitHub engine configuration migration.
 *
 * @see GitHubEngineConfigurationMigrationActionIT
 * @see GitHubEngineConfigurationMigrationAction
 */
@Component
public class TestGitHubEngineConfigurationMigrationPreparationAction implements DBMigrationAction {

    private final ObjectMapper objectMapper = ObjectMapperFactory.create();

    @Override
    public int getPatch() {
        return GitHubEngineConfigurationMigrationAction.GITHUB_ENGINE_PATCH - 1;
    }

    @Override
    public void migrate(Connection connection) throws Exception {
        // Creates a repository
        try (PreparedStatement ps =
                     connection.prepareStatement("INSERT INTO CONFIGURATIONS(TYPE, NAME, CONTENT) VALUES (?, ?, ?)")) {
            ps.setString(1, "net.nemerosa.ontrack.extension.github.model.GitHubConfiguration");
            ps.setString(2, "OntrackTest");
            ps.setString(3, objectMapper.writeValueAsString(
                            new GitHubEngineConfigurationMigrationAction.OldConfiguration(
                                    "OntrackTest",
                                    "nemerosa/ontrack",
                                    "user",
                                    "password",
                                    "token",
                                    30
                            )
                    )
            );
            ps.execute();
        }
        // Creates a project
        int projectId;
        try (PreparedStatement ps = connection.prepareStatement("INSERT INTO PROJECTS(NAME) VALUES (?)", new String[]{"ID"})) {
            ps.setString(1, "GitHubEngineConfigurationMigrationAction");
            ps.executeUpdate();
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys != null && generatedKeys.next()) {
                projectId = generatedKeys.getInt(1);
            } else {
                throw new IllegalStateException("Could not create test project");
            }
        }
        // Project GitHub property
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO PROPERTIES(TYPE, PROJECT, SEARCHKEY, JSON) VALUES (?, ?, ?, ?)"
        )) {
            ps.setString(1, GitHubProjectConfigurationPropertyType.class.getName());
            ps.setInt(2, projectId);
            ps.setString(3, "");
            ps.setString(4, objectMapper.writeValueAsString(
                    JsonUtils.object()
                            .with("configuration", "OntrackTest")
                            .end()
            ));
        }
    }

    @Override
    public String getDisplayName() {
        return "Preparing GitHubEngineConfigurationMigrationAction migration test";
    }
}
