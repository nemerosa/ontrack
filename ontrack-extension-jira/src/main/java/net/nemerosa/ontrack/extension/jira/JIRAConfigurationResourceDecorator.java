package net.nemerosa.ontrack.extension.jira;

import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@Component
public class JIRAConfigurationResourceDecorator extends AbstractResourceDecorator<JIRAConfiguration> {

    public JIRAConfigurationResourceDecorator() {
        super(JIRAConfiguration.class);
    }

    /**
     * Obfuscates the password
     */
    @Override
    public JIRAConfiguration decorateBeforeSerialization(JIRAConfiguration bean) {
        return bean.obfuscate();
    }

    @Override
    public List<Link> links(JIRAConfiguration configuration, ResourceContext resourceContext) {
        return resourceContext.links()
                .self(on(JIRAController.class).getConfiguration(configuration.getName()))
                .link(Link.DELETE, on(JIRAController.class).deleteConfiguration(configuration.getName()))
                .build();
    }
}
