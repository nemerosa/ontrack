package net.nemerosa.ontrack.extension.artifactory.property;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.common.MapBuilder;
import net.nemerosa.ontrack.extension.artifactory.ArtifactoryExtensionFeature;
import net.nemerosa.ontrack.extension.artifactory.configuration.ArtifactoryConfiguration;
import net.nemerosa.ontrack.extension.artifactory.configuration.ArtifactoryConfigurationService;
import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
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
public class ArtifactoryPromotionSyncPropertyType extends AbstractPropertyType<ArtifactoryPromotionSyncProperty>
        implements ConfigurationPropertyType<ArtifactoryConfiguration, ArtifactoryPromotionSyncProperty> {

    private final ArtifactoryConfigurationService configurationService;

    @Autowired
    public ArtifactoryPromotionSyncPropertyType(ArtifactoryExtensionFeature extensionFeature, ArtifactoryConfigurationService configurationService) {
        super(extensionFeature);
        this.configurationService = configurationService;
    }

    @Override
    public String getName() {
        return "Artifactory promotion sync";
    }

    @Override
    public String getDescription() {
        return "Synchronisation of the promotions with Artifactory build statuses";
    }

    @Override
    public Set<ProjectEntityType> getSupportedEntityTypes() {
        return EnumSet.of(ProjectEntityType.BRANCH);
    }

    @Override
    public boolean canEdit(ProjectEntity entity, SecurityService securityService) {
        return securityService.isProjectFunctionGranted(entity.projectId(), ProjectConfig.class);
    }

    @Override
    public boolean canView(ProjectEntity entity, SecurityService securityService) {
        return securityService.isProjectFunctionGranted(entity.projectId(), ProjectConfig.class);
    }

    @Override
    public JsonNode forStorage(ArtifactoryPromotionSyncProperty value) {
        return format(
                MapBuilder.params()
                        .with("configuration", value.getConfiguration().getName())
                        .with("buildName", value.getBuildName())
                        .with("buildNameFilter", value.getBuildNameFilter())
                        .with("interval", value.getInterval())
                        .get()
        );
    }

    @Override
    public ArtifactoryPromotionSyncProperty fromClient(JsonNode node) {
        return fromStorage(node);
    }

    @Override
    public ArtifactoryPromotionSyncProperty fromStorage(JsonNode node) {
        String configurationName = node.path("configuration").asText();
        String buildName = node.path("buildName").asText();
        String buildNameFilter = node.path("buildNameFilter").asText();
        int interval = node.path("interval").asInt();
        // Looks the configuration up
        ArtifactoryConfiguration configuration = configurationService.getConfiguration(configurationName);
        // Validates the project path
        validateNotBlank(buildName, "The build name must not be empty");
        // Validates the interval
        if (interval < 0) interval = 0;
        // OK
        return new ArtifactoryPromotionSyncProperty(
                configuration,
                buildName,
                buildNameFilter,
                interval
        );
    }

    @Override
    public ArtifactoryPromotionSyncProperty replaceValue(@NotNull ArtifactoryPromotionSyncProperty value, Function<String, String> replacementFunction) {
        return new ArtifactoryPromotionSyncProperty(
                configurationService.replaceConfiguration(value.getConfiguration(), replacementFunction),
                replacementFunction.apply(value.getBuildName()),
                replacementFunction.apply(value.getBuildNameFilter()),
                value.getInterval()
        );
    }

}
