package net.nemerosa.ontrack.extension.stash.property;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.common.MapBuilder;
import net.nemerosa.ontrack.extension.git.property.AbstractGitProjectConfigurationPropertyType;
import net.nemerosa.ontrack.extension.stash.StashExtensionFeature;
import net.nemerosa.ontrack.extension.stash.model.StashConfiguration;
import net.nemerosa.ontrack.extension.stash.service.StashConfigurationService;
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
public class StashProjectConfigurationPropertyType extends AbstractGitProjectConfigurationPropertyType<StashProjectConfigurationProperty>
        implements ConfigurationPropertyType<StashConfiguration, StashProjectConfigurationProperty> {

    private final StashConfigurationService configurationService;

    @Autowired
    public StashProjectConfigurationPropertyType(StashExtensionFeature extensionFeature, StashConfigurationService configurationService) {
        super(extensionFeature);
        this.configurationService = configurationService;
    }

    @Override
    public String getName() {
        return "Bitbucket Server configuration";
    }

    @Override
    public String getDescription() {
        return "Associates the project with a Bitbucket Server repository";
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
                node.path("repository").asText(),
                node.path("indexationInterval").asInt(),
                JsonUtils.get(node, "issueServiceConfigurationIdentifier", null)
        );
    }

    @Override
    public JsonNode forStorage(StashProjectConfigurationProperty value) {
        return format(
                MapBuilder.params()
                        .with("configuration", value.getConfiguration().getName())
                        .with("project", value.getProject())
                        .with("repository", value.getRepository())
                        .with("indexationInterval", value.getIndexationInterval())
                        .with("issueServiceConfigurationIdentifier", value.getIssueServiceConfigurationIdentifier())
                        .get()
        );
    }

    @Override
    public StashProjectConfigurationProperty replaceValue(@NotNull StashProjectConfigurationProperty value, Function<String, String> replacementFunction) {
        return new StashProjectConfigurationProperty(
                value.getConfiguration(),
                replacementFunction.apply(value.getProject()),
                replacementFunction.apply(value.getRepository()),
                value.getIndexationInterval(),
                value.getIssueServiceConfigurationIdentifier()
        );
    }

}
