package net.nemerosa.ontrack.extension.gitlab.property;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.common.MapBuilder;
import net.nemerosa.ontrack.extension.git.property.AbstractGitProjectConfigurationPropertyType;
import net.nemerosa.ontrack.extension.gitlab.GitLabExtensionFeature;
import net.nemerosa.ontrack.extension.gitlab.GitLabIssueServiceExtension;
import net.nemerosa.ontrack.extension.gitlab.model.GitLabConfiguration;
import net.nemerosa.ontrack.extension.gitlab.service.GitLabConfigurationService;
import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Component
public class GitLabProjectConfigurationPropertyType
        extends AbstractGitProjectConfigurationPropertyType<GitLabProjectConfigurationProperty>
        implements ConfigurationPropertyType<GitLabConfiguration, GitLabProjectConfigurationProperty> {

    private final GitLabConfigurationService configurationService;
    private final IssueServiceRegistry issueServiceRegistry;

    @Autowired
    public GitLabProjectConfigurationPropertyType(
            GitLabExtensionFeature extensionFeature,
            GitLabConfigurationService configurationService,
            IssueServiceRegistry issueServiceRegistry) {
        super(extensionFeature);
        this.configurationService = configurationService;
        this.issueServiceRegistry = issueServiceRegistry;
    }

    @Override
    public String getName() {
        return "GitLab configuration";
    }

    @Override
    public String getDescription() {
        return "Associates the project with a GitLab repository";
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
    public Form getEditionForm(ProjectEntity entity, GitLabProjectConfigurationProperty value) {
        // Gets the list of issue configurations
        List<IssueServiceConfigurationRepresentation> availableIssueServiceConfigurations =
                new ArrayList<>(
                        issueServiceRegistry.getAvailableIssueServiceConfigurations()
                );
        // Adds the configuration for THIS project
        availableIssueServiceConfigurations.add(
                0,
                IssueServiceConfigurationRepresentation.Companion.self("GitLab issues", GitLabIssueServiceExtension.GITLAB_SERVICE_ID)
        );
        // Edition form
        return Form.create()
                .with(
                        Selection.of("configuration")
                                .label("Configuration")
                                .help("GitLab configuration to use to access the repository")
                                .items(configurationService.getConfigurationDescriptors())
                                .value(value != null ? value.getConfiguration().getName() : null)
                )
                .with(
                        Selection.of("issueServiceConfigurationIdentifier")
                                .label("Issue configuration")
                                .help("Select an issue service that is used to associate tickets and issues to the source.")
                                .optional()
                                .items(availableIssueServiceConfigurations)
                                .value(value != null ? value.getIssueServiceConfigurationIdentifier() : null)
                )
                .with(
                        Text.of("repository")
                                .label("Repository")
                                .length(100)
                                .regex("[A-Za-z0-9_\\.\\-]+")
                                .validation("Repository is required and must be a GitLab repository (account/repository).")
                                .value(value != null ? value.getRepository() : null)
                )
                .with(
                        Int.of("indexationInterval")
                                .label("Indexation interval")
                                .min(0)
                                .max(60 * 24)
                                .value(value != null ? value.getIndexationInterval() : 0)
                                .help("@file:extension/gitlab/help.net.nemerosa.ontrack.extension.gitlab.model.GitLabConfiguration.indexationInterval.tpl.html")
                );

    }

    @Override
    public GitLabProjectConfigurationProperty fromClient(JsonNode node) {
        return fromStorage(node);
    }

    @Override
    public GitLabProjectConfigurationProperty fromStorage(JsonNode node) {
        String configurationName = node.path("configuration").asText();
        // Looks the configuration up
        GitLabConfiguration configuration = configurationService.getConfiguration(configurationName);
        // OK
        return new GitLabProjectConfigurationProperty(
                configuration,
                JsonUtils.get(node, "issueServiceConfigurationIdentifier", null),
                node.path("repository").asText(),
                node.path("indexationInterval").asInt()
        );
    }

    @Override
    public JsonNode forStorage(GitLabProjectConfigurationProperty value) {
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
    public String getSearchKey(GitLabProjectConfigurationProperty value) {
        return value.getConfiguration().getName();
    }

    @Override
    public GitLabProjectConfigurationProperty replaceValue(GitLabProjectConfigurationProperty value, Function<String, String> replacementFunction) {
        return new GitLabProjectConfigurationProperty(
                configurationService.replaceConfiguration(value.getConfiguration(), replacementFunction),
                value.getIssueServiceConfigurationIdentifier(),
                replacementFunction.apply(value.getRepository()),
                value.getIndexationInterval()
        );
    }

}
