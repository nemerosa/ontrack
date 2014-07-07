package net.nemerosa.ontrack.extension.artifactory.property;

import net.nemerosa.ontrack.extension.artifactory.ArtifactoryExtensionFeature;
import net.nemerosa.ontrack.extension.artifactory.configuration.ArtifactoryConfigurationService;
import net.nemerosa.ontrack.extension.support.AbstractPropertyTypeExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ArtifactoryPromotionSyncPropertyTypeExtension extends AbstractPropertyTypeExtension<ArtifactoryPromotionSyncProperty> {

    @Autowired
    public ArtifactoryPromotionSyncPropertyTypeExtension(ArtifactoryExtensionFeature extensionFeature, ArtifactoryConfigurationService configurationService) {
        super(extensionFeature, new ArtifactoryPromotionSyncPropertyType(configurationService));
    }

}
