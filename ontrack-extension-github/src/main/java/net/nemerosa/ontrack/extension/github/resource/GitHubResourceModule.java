package net.nemerosa.ontrack.extension.github.resource;

import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.ui.resource.AbstractResourceModule;
import net.nemerosa.ontrack.ui.resource.ResourceDecorator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;

/**
 * Declaration of resource bindings for the Jenkins extension.
 */
@Component
public class GitHubResourceModule extends AbstractResourceModule {

    private final SecurityService securityService;

    @Autowired
    public GitHubResourceModule(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    public Collection<ResourceDecorator<?>> decorators() {
        return Arrays.asList(
                new GitHubEngineConfigurationResourceDecorator(securityService)
        );
    }

}
