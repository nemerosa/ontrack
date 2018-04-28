package net.nemerosa.ontrack.extension.svn.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.nemerosa.ontrack.client.JsonClientMappingException;
import net.nemerosa.ontrack.extension.scm.support.TagPattern;
import net.nemerosa.ontrack.extension.svn.model.SVNConfiguration;
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationPropertyType;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.model.structure.ServiceConfiguration;
import net.nemerosa.ontrack.model.support.DBMigrationAction;
import net.nemerosa.ontrack.model.support.NoConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Migration of {@link net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationProperty}
 * to the use of {@link net.nemerosa.ontrack.extension.svn.model.BuildSvnRevisionLink}.
 */
@Component
public class BuildSvnRevisionLinkMigrationAction implements DBMigrationAction {

    public static final String BUILD_PLACEHOLDER_PATTERN = "\\{(.+)\\}";

    private final Logger logger = LoggerFactory.getLogger(BuildSvnRevisionLinkMigrationAction.class);
    private final ObjectMapper objectMapper = ObjectMapperFactory.create();

    private final RevisionSvnRevisionLink revisionLink;
    private final TagNamePatternSvnRevisionLink tagPatternLink;
    private final TagNameSvnRevisionLink tagLink;

    @Autowired
    public BuildSvnRevisionLinkMigrationAction(RevisionSvnRevisionLink revisionLink, TagNamePatternSvnRevisionLink tagPatternLink, TagNameSvnRevisionLink tagLink) {
        this.revisionLink = revisionLink;
        this.tagPatternLink = tagPatternLink;
        this.tagLink = tagLink;
    }

    @Override
    public int getPatch() {
        return 23;
    }

    @Override
    public void migrate(Connection connection) throws Exception {
        // For all SVN configurations
        migrateSvnConfigurations(connection);
        // For all Svn branch configurations
        migrateSvnBranchConfigurations(connection);
    }

    private void migrateSvnConfigurations(Connection connection) throws SQLException, IOException {
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM CONFIGURATIONS WHERE TYPE = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
            ps.setString(1, SVNConfiguration.class.getName());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Configuration as JSON
                    String json = rs.getString("CONTENT");
                    // Parses the configuration as JSON
                    ObjectNode node = (ObjectNode) objectMapper.readTree(json);
                    // Migrates the node
                    migrateSvnConfiguration(node);
                    // Updating
                    rs.updateString("CONTENT", objectMapper.writeValueAsString(node));
                    rs.updateRow();
                }
            }
        }
    }

    private void migrateSvnBranchConfigurations(Connection connection) throws SQLException, IOException {
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM PROPERTIES WHERE TYPE = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
            ps.setString(1, SVNBranchConfigurationPropertyType.class.getName());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Configuration as JSON
                    String json = rs.getString("JSON");
                    // Parses the configuration as JSON
                    ObjectNode node = (ObjectNode) objectMapper.readTree(json);
                    // Migrates the node
                    migrateSvnBranchConfiguration(node);
                    // Updating
                    rs.updateString("JSON", objectMapper.writeValueAsString(node));
                    rs.updateRow();
                }
            }
        }
    }

    protected void migrateSvnConfiguration(ObjectNode node) {
        // Removes branch & tag patterns
        node.remove("branchPattern");
        node.remove("tagPattern");
    }

    protected void migrateSvnBranchConfiguration(ObjectNode node) {
        // Gets the build path & branch path
        String branchPath = node.get("branchPath").asText();
        String buildPath = node.get("buildPath").asText();
        // Removes the build path property
        node.remove("buildPath");
        // Converts to a service configuration
        ConfiguredBuildSvnRevisionLink<?> configuredBuildSvnRevisionLink = toBuildSvnRevisionLinkConfiguration(
                buildPath
        );
        // Gets the configuration representation
        ServiceConfiguration serviceConfiguration = configuredBuildSvnRevisionLink.toServiceConfiguration();
        // As json...
        node.set("buildRevisionLink", (ObjectNode) objectMapper.valueToTree(serviceConfiguration));
        // Logging
        try {
            logger.info(
                    "SVN branch config for {} with build expression {} has been converted to {}",
                    branchPath,
                    buildPath,
                    objectMapper.writeValueAsString(serviceConfiguration)
            );
        } catch (IOException ex) {
            throw new JsonClientMappingException(ex);
        }
    }

    protected ConfiguredBuildSvnRevisionLink<?> toBuildSvnRevisionLinkConfiguration(String buildPath) {
        // Revision based
        if (buildPath.endsWith("@{build}")) {
            return new ConfiguredBuildSvnRevisionLink<>(
                    revisionLink,
                    NoConfig.INSTANCE
            );
        }
        // Looking for the {build} expression
        Pattern pattern = Pattern.compile(BUILD_PLACEHOLDER_PATTERN);
        Matcher matcher = pattern.matcher(buildPath);
        if (matcher.find()) {
            String expression = matcher.group(1);
            if ("build".equals(expression)) {
                return new ConfiguredBuildSvnRevisionLink<>(
                        tagLink,
                        NoConfig.INSTANCE
                );
            } else if (StringUtils.startsWith(expression, "build:")) {
                String buildExpression = StringUtils.substringAfter(expression, "build:");
                return new ConfiguredBuildSvnRevisionLink<>(
                        tagPatternLink,
                        new TagPattern(buildExpression)
                );
            }
        }
        // Default
        return new ConfiguredBuildSvnRevisionLink<>(
                tagLink,
                NoConfig.INSTANCE
        );
    }

    @Override
    public String getDisplayName() {
        return "Migration of SVN branch configurations to the use of revision links";
    }
}
