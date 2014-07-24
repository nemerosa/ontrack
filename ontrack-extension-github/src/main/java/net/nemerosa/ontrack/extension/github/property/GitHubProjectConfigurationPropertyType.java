package net.nemerosa.ontrack.extension.github.property;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.common.MapBuilder;
import net.nemerosa.ontrack.extension.github.model.GitHubConfiguration;
import net.nemerosa.ontrack.extension.github.service.GitHubConfigurationService;
import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Selection;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;

import java.util.EnumSet;
import java.util.Set;

public class GitHubProjectConfigurationPropertyType extends AbstractPropertyType<GitHubProjectConfigurationProperty> {

    private final GitHubConfigurationService configurationService;

    public GitHubProjectConfigurationPropertyType(GitHubConfigurationService configurationService) {
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
    public Form getEditionForm(GitHubProjectConfigurationProperty value) {
        return Form.create()
                .with(
                        Selection.of("configuration")
                                .label("Configuration")
                                .help("GitHub configuration to use to access the repository")
                                .items(configurationService.getConfigurationDescriptors())
                                .value(value != null ? value.getConfiguration().getName() : null)
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
        GitHubConfiguration configuration = configurationService.getConfiguration(configurationName);
        // OK
        return new GitHubProjectConfigurationProperty(
                configuration
        );
    }

    @Override
    public JsonNode forStorage(GitHubProjectConfigurationProperty value) {
        return format(
                MapBuilder.params()
                        .with("configuration", value.getConfiguration().getName())
                        .get()
        );
    }

    @Override
    public String getSearchKey(GitHubProjectConfigurationProperty value) {
        return value.getConfiguration().getName();
    }
}
