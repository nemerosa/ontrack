package net.nemerosa.ontrack.extension.artifactory.resource;

import net.nemerosa.ontrack.extension.artifactory.ArtifactoryController;
import net.nemerosa.ontrack.extension.artifactory.configuration.ArtifactoryConfiguration;
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@Component
public class ArtifactoryConfigurationResourceDecorator extends AbstractResourceDecorator<ArtifactoryConfiguration> {

    public ArtifactoryConfigurationResourceDecorator() {
        super(ArtifactoryConfiguration.class);
    }

    /**
     * Obfuscates the password
     */
    @Override
    public ArtifactoryConfiguration decorateBeforeSerialization(ArtifactoryConfiguration bean) {
        return bean.obfuscate();
    }

    @Override
    public List<Link> links(ArtifactoryConfiguration configuration, ResourceContext resourceContext) {
        return resourceContext.links()
                .self(on(ArtifactoryController.class).getConfiguration(configuration.getName()))
                .link(Link.DELETE, on(ArtifactoryController.class).deleteConfiguration(configuration.getName()))
                .build();
    }
}
