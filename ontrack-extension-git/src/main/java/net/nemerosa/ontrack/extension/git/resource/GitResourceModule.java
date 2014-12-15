package net.nemerosa.ontrack.extension.git.resource;

import net.nemerosa.ontrack.extension.git.service.GitService;
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
public class GitResourceModule extends AbstractResourceModule {

    private final GitService gitService;
    private final SecurityService securityService;

    @Autowired
    public GitResourceModule(GitService gitService, SecurityService securityService) {
        this.gitService = gitService;
        this.securityService = securityService;
    }

    @Override
    public Collection<ResourceDecorator<?>> decorators() {
        return Arrays.asList(
                new BasicGitConfigurationResourceDecorator(securityService),
                new GitChangeLogResourceDecorator(gitService)
        );
    }

}
