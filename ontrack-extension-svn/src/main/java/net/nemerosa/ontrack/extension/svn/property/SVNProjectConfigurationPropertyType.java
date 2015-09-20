package net.nemerosa.ontrack.extension.svn.property;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.common.MapBuilder;
import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
import net.nemerosa.ontrack.extension.svn.model.SVNConfiguration;
import net.nemerosa.ontrack.extension.svn.service.SVNConfigurationService;
import net.nemerosa.ontrack.model.form.Form;
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
public class SVNProjectConfigurationPropertyType extends AbstractPropertyType<SVNProjectConfigurationProperty>
        implements ConfigurationPropertyType<SVNConfiguration, SVNProjectConfigurationProperty> {

    private final SVNConfigurationService configurationService;

    @Autowired
    public SVNProjectConfigurationPropertyType(SVNConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Override
    public String getName() {
        return "SVN configuration";
    }

    @Override
    public String getDescription() {
        return "Associates the project with a Subversion repository";
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
    public Form getEditionForm(ProjectEntity entity, SVNProjectConfigurationProperty value) {
        return Form.create()
                .with(
                        Selection.of("configuration")
                                .label("Configuration")
                                .help("SVN configuration to use to access the repository")
                                .items(configurationService.getConfigurationDescriptors())
                                .value(value != null ? value.getConfiguration().getName() : null)
                ).with(
                        Text.of("projectPath")
                                .label("Project path")
                                .help("Path of the main project branch (trunk) in this configuration. The path is relative to the root " +
                                        "of the repository.")
                                .value(value != null ? value.getProjectPath() : "/project/trunk")
                );
    }

    @Override
    public JsonNode forStorage(SVNProjectConfigurationProperty value) {
        return format(
                MapBuilder.params()
                        .with("configuration", value.getConfiguration().getName())
                        .with("projectPath", value.getProjectPath())
                        .get()
        );
    }

    @Override
    public SVNProjectConfigurationProperty fromClient(JsonNode node) {
        return fromStorage(node);
    }

    @Override
    public SVNProjectConfigurationProperty fromStorage(JsonNode node) {
        String configurationName = node.path("configuration").asText();
        String projectPath = node.path("projectPath").asText();
        // Looks the configuration up
        SVNConfiguration configuration = configurationService.getConfiguration(configurationName);
        // Validates the project path
        validateNotBlank(projectPath, "The project path must not be empty");
        // OK
        return new SVNProjectConfigurationProperty(
                configuration,
                projectPath
        );
    }

    @Override
    public String getSearchKey(SVNProjectConfigurationProperty value) {
        return value.getConfiguration().getName();
    }

    @Override
    public SVNProjectConfigurationProperty replaceValue(SVNProjectConfigurationProperty value, Function<String, String> replacementFunction) {
        return new SVNProjectConfigurationProperty(
                configurationService.replaceConfiguration(value.getConfiguration(), replacementFunction),
                replacementFunction.apply(value.getProjectPath())
        );
    }

}
