package net.nemerosa.ontrack.extension.gitlab.property;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.common.MapBuilder;
import net.nemerosa.ontrack.extension.git.property.AbstractGitProjectConfigurationPropertyType;
import net.nemerosa.ontrack.extension.gitlab.GitLabExtensionFeature;
import net.nemerosa.ontrack.extension.gitlab.model.GitLabConfiguration;
import net.nemerosa.ontrack.extension.gitlab.service.GitLabConfigurationService;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.model.support.ConfigurationPropertyType;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Function;

@Component
public class GitLabProjectConfigurationPropertyType
        extends AbstractGitProjectConfigurationPropertyType<GitLabProjectConfigurationProperty>
        implements ConfigurationPropertyType<GitLabConfiguration, GitLabProjectConfigurationProperty> {

    private final GitLabConfigurationService configurationService;

    @Autowired
    public GitLabProjectConfigurationPropertyType(
            GitLabExtensionFeature extensionFeature,
            GitLabConfigurationService configurationService) {
        super(extensionFeature);
        this.configurationService = configurationService;
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
    public GitLabProjectConfigurationProperty replaceValue(@NotNull GitLabProjectConfigurationProperty value, Function<String, String> replacementFunction) {
        return new GitLabProjectConfigurationProperty(
                configurationService.replaceConfiguration(value.getConfiguration(), replacementFunction),
                value.getIssueServiceConfigurationIdentifier(),
                replacementFunction.apply(value.getRepository()),
                value.getIndexationInterval()
        );
    }

}
