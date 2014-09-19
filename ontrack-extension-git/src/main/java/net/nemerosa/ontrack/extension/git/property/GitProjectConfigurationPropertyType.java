package net.nemerosa.ontrack.extension.git.property;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.common.MapBuilder;
import net.nemerosa.ontrack.extension.git.model.GitConfiguration;
import net.nemerosa.ontrack.extension.git.service.GitConfigurationService;
import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Selection;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Function;

public class GitProjectConfigurationPropertyType extends AbstractPropertyType<GitProjectConfigurationProperty> {

    private final GitConfigurationService configurationService;

    public GitProjectConfigurationPropertyType(GitConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Override
    public String getName() {
        return "Git configuration";
    }

    @Override
    public String getDescription() {
        return "Associates the project with a Git repository";
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
    public Form getEditionForm(GitProjectConfigurationProperty value) {
        return Form.create()
                .with(
                        Selection.of("configuration")
                                .label("Configuration")
                                .help("Git configuration to use to access the repository")
                                .items(configurationService.getConfigurationDescriptors())
                                .value(value != null ? value.getConfiguration().getName() : null)
                );
    }

    @Override
    public GitProjectConfigurationProperty fromClient(JsonNode node) {
        return fromStorage(node);
    }

    @Override
    public GitProjectConfigurationProperty fromStorage(JsonNode node) {
        String configurationName = node.path("configuration").asText();
        // Looks the configuration up
        GitConfiguration configuration = configurationService.getConfiguration(configurationName);
        // OK
        return new GitProjectConfigurationProperty(
                configuration
        );
    }

    @Override
    public JsonNode forStorage(GitProjectConfigurationProperty value) {
        return format(
                MapBuilder.params()
                        .with("configuration", value.getConfiguration().getName())
                        .get()
        );
    }

    @Override
    public String getSearchKey(GitProjectConfigurationProperty value) {
        return value.getConfiguration().getName();
    }

    @Override
    public GitProjectConfigurationProperty replaceValue(GitProjectConfigurationProperty value, Function<String, String> replacementFunction) {
        return new GitProjectConfigurationProperty(
                configurationService.replaceConfiguration(value.getConfiguration(), replacementFunction)
        );
    }

}
