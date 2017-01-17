package net.nemerosa.ontrack.extension.svn.resource;

import net.nemerosa.ontrack.extension.svn.SVNController;
import net.nemerosa.ontrack.extension.svn.model.SVNConfiguration;
import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@Component
public class SVNConfigurationResourceDecorator extends AbstractResourceDecorator<SVNConfiguration> {

    private final SecurityService securityService;

    @Autowired
    public SVNConfigurationResourceDecorator(SecurityService securityService) {
        super(SVNConfiguration.class);
        this.securityService = securityService;
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
        boolean globalSettingsGranted = securityService.isGlobalFunctionGranted(GlobalSettings.class);
        return resourceContext.links()
                .self(on(SVNController.class).getConfiguration(configuration.getName()))
                .link(Link.UPDATE, on(SVNController.class).updateConfigurationForm(configuration.getName()))
                .link(Link.DELETE, on(SVNController.class).deleteConfiguration(configuration.getName()))
                // Indexation
                .link("_indexation", on(SVNController.class).getLastRevisionInfo(configuration.getName()), globalSettingsGranted)
                .link("_indexationFromLatest", on(SVNController.class).indexFromLatest(configuration.getName()), globalSettingsGranted)
                .link("_indexationFull", on(SVNController.class).full(configuration.getName()), globalSettingsGranted)
                // OK
                .build();
    }
}
