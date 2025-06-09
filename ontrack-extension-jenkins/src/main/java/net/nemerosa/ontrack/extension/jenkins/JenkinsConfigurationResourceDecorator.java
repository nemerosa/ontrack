package net.nemerosa.ontrack.extension.jenkins;

import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@Component
public class JenkinsConfigurationResourceDecorator extends AbstractResourceDecorator<JenkinsConfiguration> {

    public JenkinsConfigurationResourceDecorator() {
        super(JenkinsConfiguration.class);
    }

    /**
     * Obfuscates the password
     */
    @Override
    public JenkinsConfiguration decorateBeforeSerialization(JenkinsConfiguration bean) {
        return bean.obfuscate();
    }

    @Override
    public List<Link> links(JenkinsConfiguration configuration, ResourceContext resourceContext) {
        return resourceContext.links()
                .self(on(JenkinsController.class).getConfiguration(configuration.getName()))
                .link(Link.DELETE, on(JenkinsController.class).deleteConfiguration(configuration.getName()))
                .build();
    }
}
