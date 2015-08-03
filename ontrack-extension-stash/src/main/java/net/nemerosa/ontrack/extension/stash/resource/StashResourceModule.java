package net.nemerosa.ontrack.extension.stash.resource;

import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.ui.resource.AbstractResourceModule;
import net.nemerosa.ontrack.ui.resource.ResourceDecorator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

/**
 * Declaration of resource bindings for the extension.
 */
@Component
public class StashResourceModule extends AbstractResourceModule {

    private final SecurityService securityService;

    @Autowired
    public StashResourceModule(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    public Collection<ResourceDecorator<?>> decorators() {
        return Collections.singletonList(
                new StashConfigurationResourceDecorator(securityService)
        );
    }

}
