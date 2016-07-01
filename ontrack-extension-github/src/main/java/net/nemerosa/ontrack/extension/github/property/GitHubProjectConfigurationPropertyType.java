package net.nemerosa.ontrack.extension.github.property;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.common.MapBuilder;
import net.nemerosa.ontrack.extension.git.property.AbstractGitProjectConfigurationPropertyType;
import net.nemerosa.ontrack.extension.github.GitHubExtensionFeature;
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration;
import net.nemerosa.ontrack.extension.github.service.GitHubConfigurationService;
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

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Function;

@Component
public class GitHubProjectConfigurationPropertyType
        extends AbstractGitProjectConfigurationPropertyType<GitHubProjectConfigurationProperty>
        implements ConfigurationPropertyType<GitHubEngineConfiguration, GitHubProjectConfigurationProperty> {

    private final GitHubConfigurationService configurationService;

    @Autowired
    public GitHubProjectConfigurationPropertyType(GitHubExtensionFeature extensionFeature, GitHubConfigurationService configurationService) {
        super(extensionFeature);
        this.configurationService = configurationService;
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
                );

    }

    @Override
    public GitHubProjectConfigurationProperty fromClient(JsonNode node) {
        return fromStorage(node);
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
                node.path("indexationInterval").asInt()
        );
    }

    @Override
    public JsonNode forStorage(GitHubProjectConfigurationProperty value) {
        return format(
                MapBuilder.params()
                        .with("configuration", value.getConfiguration().getName())
                        .with("repository", value.getRepository())
                        .with("indexationInterval", value.getIndexationInterval())
                        .get()
        );
    }

    @Override
    public String getSearchKey(GitHubProjectConfigurationProperty value) {
        return value.getConfiguration().getName();
    }

    @Override
    public GitHubProjectConfigurationProperty replaceValue(GitHubProjectConfigurationProperty value, Function<String, String> replacementFunction) {
        return new GitHubProjectConfigurationProperty(
                configurationService.replaceConfiguration(value.getConfiguration(), replacementFunction),
                replacementFunction.apply(value.getRepository()),
                value.getIndexationInterval()
        );
    }

}
