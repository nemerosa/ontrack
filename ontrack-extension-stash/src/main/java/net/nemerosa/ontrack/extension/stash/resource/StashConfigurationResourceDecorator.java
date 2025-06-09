package net.nemerosa.ontrack.extension.stash.resource;

import net.nemerosa.ontrack.extension.stash.StashController;
import net.nemerosa.ontrack.extension.stash.model.StashConfiguration;
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
public class StashConfigurationResourceDecorator extends AbstractResourceDecorator<StashConfiguration> {

    private final SecurityService securityService;

    @Autowired
    public StashConfigurationResourceDecorator(SecurityService securityService) {
        super(StashConfiguration.class);
        this.securityService = securityService;
    }

    /**
     * Obfuscates the password
     */
    @Override
    public StashConfiguration decorateBeforeSerialization(StashConfiguration bean) {
        return bean.obfuscate();
    }

    @Override
    public List<Link> links(StashConfiguration configuration, ResourceContext resourceContext) {
        boolean globalSettingsGranted = securityService.isGlobalFunctionGranted(GlobalSettings.class);
        return resourceContext.links()
                .self(on(StashController.class).getConfiguration(configuration.getName()))
                .link(Link.DELETE, on(StashController.class).deleteConfiguration(configuration.getName()), globalSettingsGranted)
                        // OK
                .build();
    }
}
