package net.nemerosa.ontrack.extension.artifactory;

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature;
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions;
import org.springframework.stereotype.Component;

@Component
public class ArtifactoryExtensionFeature extends AbstractExtensionFeature {

    public ArtifactoryExtensionFeature() {
        super("artifactory", "Artifactory", "Support for Artifactory",
                ExtensionFeatureOptions.DEFAULT.withGui(true));
    }
}
