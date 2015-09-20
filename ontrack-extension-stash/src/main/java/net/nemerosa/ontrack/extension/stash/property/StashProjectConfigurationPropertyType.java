package net.nemerosa.ontrack.extension.stash.property;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.common.MapBuilder;
import net.nemerosa.ontrack.extension.stash.model.StashConfiguration;
import net.nemerosa.ontrack.extension.stash.service.StashConfigurationService;
import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
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
public class StashProjectConfigurationPropertyType extends AbstractPropertyType<StashProjectConfigurationProperty>
        implements ConfigurationPropertyType<StashConfiguration, StashProjectConfigurationProperty> {

    private final StashConfigurationService configurationService;

    @Autowired
    public StashProjectConfigurationPropertyType(StashConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Override
    public String getName() {
        return "Stash configuration";
    }

    @Override
    public String getDescription() {
        return "Associates the project with a Stash repository";
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
    public Form getEditionForm(ProjectEntity entity, StashProjectConfigurationProperty value) {
        return Form.create()
                .with(
                        Selection.of("configuration")
                                .label("Configuration")
                                .help("Stash configuration to use to access the repository")
                                .items(configurationService.getConfigurationDescriptors())
                                .value(value != null ? value.getConfiguration().getName() : null)
                )
                .with(
                        Text.of("project")
                                .label("Project")
                                .help("ID of the Stash project")
                )
                .with(
                        Text.of("repository")
                                .label("Repository")
                                .help("Repository in the Stash project")
                )
                ;
    }

    @Override
    public StashProjectConfigurationProperty fromClient(JsonNode node) {
        return fromStorage(node);
    }

    @Override
    public StashProjectConfigurationProperty fromStorage(JsonNode node) {
        String configurationName = node.path("configuration").asText();
        // Looks the configuration up
        StashConfiguration configuration = configurationService.getConfiguration(configurationName);
        // OK
        return new StashProjectConfigurationProperty(
                configuration,
                node.path("project").asText(),
                node.path("repository").asText()
        );
    }

    @Override
    public JsonNode forStorage(StashProjectConfigurationProperty value) {
        return format(
                MapBuilder.params()
                        .with("configuration", value.getConfiguration().getName())
                        .with("project", value.getProject())
                        .with("repository", value.getRepository())
                        .get()
        );
    }

    @Override
    public String getSearchKey(StashProjectConfigurationProperty value) {
        return value.getConfiguration().getName();
    }

    @Override
    public StashProjectConfigurationProperty replaceValue(StashProjectConfigurationProperty value, Function<String, String> replacementFunction) {
        return new StashProjectConfigurationProperty(
                configurationService.replaceConfiguration(value.getConfiguration(), replacementFunction),
                replacementFunction.apply(value.getProject()),
                replacementFunction.apply(value.getRepository())
        );
    }

}
