package net.nemerosa.ontrack.extension.git.resource;

import net.nemerosa.ontrack.extension.git.GitController;
import net.nemerosa.ontrack.extension.git.model.BasicGitConfiguration;
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@Component
public class BasicGitConfigurationResourceDecorator extends AbstractResourceDecorator<BasicGitConfiguration> {

    public BasicGitConfigurationResourceDecorator() {
        super(BasicGitConfiguration.class);
    }

    /**
     * Obfuscates the password
     */
    @Override
    public BasicGitConfiguration decorateBeforeSerialization(BasicGitConfiguration bean) {
        return bean.obfuscate();
    }

    @Override
    public List<Link> links(BasicGitConfiguration configuration, ResourceContext resourceContext) {
        return resourceContext.links()
                .self(on(GitController.class).getConfiguration(configuration.getName()))
                .link(Link.DELETE, on(GitController.class).deleteConfiguration(configuration.getName()))
                        // OK
                .build();
    }
}
