package net.nemerosa.ontrack.extension.artifactory.resource;

import net.nemerosa.ontrack.ui.resource.AbstractResourceModule;
import net.nemerosa.ontrack.ui.resource.ResourceDecorator;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;

/**
 * Declaration of resource bindings for the Artifactory extension.
 */
@Component
public class ArtifactoryResourceModule extends AbstractResourceModule {

    @Override
    public Collection<ResourceDecorator<?>> decorators() {
        return Arrays.asList(
                new ArtifactoryConfigurationResourceDecorator()
        );
    }

}
