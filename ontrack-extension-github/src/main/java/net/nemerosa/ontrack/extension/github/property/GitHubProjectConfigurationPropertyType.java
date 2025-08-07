package net.nemerosa.ontrack.extension.github.property;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.common.MapBuilder;
import net.nemerosa.ontrack.extension.git.property.AbstractGitProjectConfigurationPropertyType;
import net.nemerosa.ontrack.extension.github.GitHubExtensionFeature;
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration;
import net.nemerosa.ontrack.extension.github.service.GitHubConfigurationService;
import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry;
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationIdentifierNotFoundException;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.model.support.ConfigurationPropertyType;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Function;

@Component
public class GitHubProjectConfigurationPropertyType
        extends AbstractGitProjectConfigurationPropertyType<GitHubProjectConfigurationProperty>
        implements ConfigurationPropertyType<GitHubEngineConfiguration, GitHubProjectConfigurationProperty> {

    private final GitHubConfigurationService configurationService;
    private final IssueServiceRegistry issueServiceRegistry;

    @Autowired
    public GitHubProjectConfigurationPropertyType(GitHubExtensionFeature extensionFeature, GitHubConfigurationService configurationService, IssueServiceRegistry issueServiceRegistry) {
        super(extensionFeature);
        this.configurationService = configurationService;
        this.issueServiceRegistry = issueServiceRegistry;
    }

    @Override
    public String getName() {
        return "GitHub configuration";
    }

    @Override
    public String getDescription() {
        return "Associates the project with a GitHub repository";
    }

    @Override
    public Set<ProjectEntityType> getSupportedEntityTypes() {
        return EnumSet.of(ProjectEntityType.PROJECT);
    }

    @Override
    public boolean canEdit(ProjectEntity entity, SecurityService securityService) {
        return securityService.isProjectFunctionGranted(entity.projectId(), ProjectConfig.class);
    }

    @Override
    public boolean canView(ProjectEntity entity, SecurityService securityService) {
        return true;
    }

    @Override
    public GitHubProjectConfigurationProperty fromClient(JsonNode node) {
        GitHubProjectConfigurationProperty property = fromStorage(node);
        // Checks the issue service configuration
        String issueServiceConfigurationIdentifier = property.getIssueServiceConfigurationIdentifier();
        if (StringUtils.isNotBlank(issueServiceConfigurationIdentifier) && !IssueServiceConfigurationRepresentation.isSelf(issueServiceConfigurationIdentifier)) {
            ConfiguredIssueService configuredIssueService = issueServiceRegistry.getConfiguredIssueService(issueServiceConfigurationIdentifier);
            if (configuredIssueService == null) {
                throw new IssueServiceConfigurationIdentifierNotFoundException(issueServiceConfigurationIdentifier);
            }
        }
        // OK
        return property;
    }

    @Override
    public GitHubProjectConfigurationProperty fromStorage(JsonNode node) {
        String configurationName = node.path("configuration").asText();
        // Looks the configuration up
        GitHubEngineConfiguration configuration = configurationService.getConfiguration(configurationName);
        // OK
        return new GitHubProjectConfigurationProperty(
                configuration,
                node.path("repository").asText(),
                node.path("indexationInterval").asInt(),
                JsonUtils.get(node, "issueServiceConfigurationIdentifier", null)
        );
    }

    @Override
    public JsonNode forStorage(GitHubProjectConfigurationProperty value) {
        return format(
                MapBuilder.params()
                        .with("configuration", value.getConfiguration().getName())
                        .with("repository", value.getRepository())
                        .with("indexationInterval", value.getIndexationInterval())
                        .with("issueServiceConfigurationIdentifier", value.getIssueServiceConfigurationIdentifier())
                        .get()
        );
    }

    @Override
    public GitHubProjectConfigurationProperty replaceValue(@NotNull GitHubProjectConfigurationProperty value, Function<String, String> replacementFunction) {
        return new GitHubProjectConfigurationProperty(
                value.getConfiguration(),
                replacementFunction.apply(value.getRepository()),
                value.getIndexationInterval(),
                value.getIssueServiceConfigurationIdentifier()
        );
    }

}
