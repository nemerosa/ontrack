package net.nemerosa.ontrack.extension.github.property;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.common.MapBuilder;
import net.nemerosa.ontrack.extension.git.property.AbstractGitProjectConfigurationPropertyType;
import net.nemerosa.ontrack.extension.github.GitHubExtensionFeature;
import net.nemerosa.ontrack.extension.github.GitHubIssueServiceExtension;
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration;
import net.nemerosa.ontrack.extension.github.service.GitHubConfigurationService;
import net.nemerosa.ontrack.extension.issues.IssueServiceExtension;
import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry;
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationIdentifierNotFoundException;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Int;
import net.nemerosa.ontrack.model.form.Selection;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.model.support.ConfigurationPropertyType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
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
    public Form getEditionForm(ProjectEntity entity, GitHubProjectConfigurationProperty value) {
        // Gets the list of issue configurations
        List<IssueServiceConfigurationRepresentation> availableIssueServiceConfigurations =
                new ArrayList<>(
                        issueServiceRegistry.getAvailableIssueServiceConfigurations()
                );
        // Adds the configuration for THIS project
        availableIssueServiceConfigurations.add(
                0,
                IssueServiceConfigurationRepresentation.Companion.self("GitHub issues", GitHubIssueServiceExtension.GITHUB_SERVICE_ID)
        );
        return Form.create()
                .with(
                        Selection.of("configuration")
                                .label("Configuration")
                                .help("GitHub configuration to use to access the repository")
                                .items(configurationService.getConfigurationDescriptors())
                                .value(value != null ? value.getConfiguration().getName() : null)
                )
                .with(
                        Text.of("repository")
                                .label("Repository")
                                .length(100)
                                .regex("[A-Za-z0-9_\\.\\-]+")
                                .validation("Repository is required and must be a GitHub repository (account/repository).")
                                .value(value != null ? value.getRepository() : null)
                )
                .with(
                        Int.of("indexationInterval")
                                .label("Indexation interval")
                                .min(0)
                                .max(60 * 24)
                                .value(value != null ? value.getIndexationInterval() : 0)
                                .help("@file:extension/github/help.net.nemerosa.ontrack.extension.github.model.GitHubConfiguration.indexationInterval.tpl.html")
                )
                .with(
                        Selection.of("issueServiceConfigurationIdentifier")
                                .label("Issue configuration")
                                .help("Select an issue service that is used to associate tickets and issues to the source. " +
                                        "If none is selected, the GitHub issues for this repository are used.")
                                .optional()
                                .items(availableIssueServiceConfigurations)
                                .value(value != null ? value.getIssueServiceConfigurationIdentifier() : null)
                );

    }

    @Override
    public GitHubProjectConfigurationProperty fromClient(JsonNode node) {
        GitHubProjectConfigurationProperty property = fromStorage(node);
        // Checks the issue service configuration
        String issueServiceConfigurationIdentifier = property.getIssueServiceConfigurationIdentifier();
        if (StringUtils.isNotBlank(issueServiceConfigurationIdentifier)) {
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
    public GitHubProjectConfigurationProperty replaceValue(GitHubProjectConfigurationProperty value, Function<String, String> replacementFunction) {
        return new GitHubProjectConfigurationProperty(
                configurationService.replaceConfiguration(value.getConfiguration(), replacementFunction),
                replacementFunction.apply(value.getRepository()),
                value.getIndexationInterval(),
                value.getIssueServiceConfigurationIdentifier()
        );
    }

}
