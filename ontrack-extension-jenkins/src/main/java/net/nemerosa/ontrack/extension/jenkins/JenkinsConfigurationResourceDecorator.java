package net.nemerosa.ontrack.extension.jenkins;

import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;

import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

public class JenkinsConfigurationResourceDecorator extends AbstractResourceDecorator<JenkinsConfiguration> {

    public JenkinsConfigurationResourceDecorator() {
        super(JenkinsConfiguration.class);
    }

    // TODO Obfuscation at decoration level

    @Override
    public List<Link> links(JenkinsConfiguration configuration, ResourceContext resourceContext) {
        return resourceContext.links()
                .self(on(JenkinsController.class).getConfiguration(configuration.getName()))
                .link(Link.UPDATE, on(JenkinsController.class).updateConfigurationForm(configuration.getName()))
                .link(Link.DELETE, on(JenkinsController.class).deleteConfiguration(configuration.getName()))
                .build();
    }
}
