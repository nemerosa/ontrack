package net.nemerosa.ontrack.extension.svn;

import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;

import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

public class SVNConfigurationResourceDecorator extends AbstractResourceDecorator<SVNConfiguration> {

    public SVNConfigurationResourceDecorator() {
        super(SVNConfiguration.class);
    }

    /**
     * Obfuscates the password
     */
    @Override
    public SVNConfiguration decorateBeforeSerialization(SVNConfiguration bean) {
        return bean.obfuscate();
    }

    @Override
    public List<Link> links(SVNConfiguration configuration, ResourceContext resourceContext) {
        return resourceContext.links()
                .self(on(SVNController.class).getConfiguration(configuration.getName()))
                .link(Link.UPDATE, on(SVNController.class).updateConfigurationForm(configuration.getName()))
                .link(Link.DELETE, on(SVNController.class).deleteConfiguration(configuration.getName()))
                        // TODO Rights for indexation
                        // Indexation
                .link("_indexation", on(SVNController.class).getLastRevisionInfo(configuration.getName()))
                .link("_indexationFull", on(SVNController.class).full(configuration.getName()))
                        // OK
                .build();
    }
}
