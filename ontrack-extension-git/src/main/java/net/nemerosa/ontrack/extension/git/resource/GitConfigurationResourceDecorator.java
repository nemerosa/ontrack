package net.nemerosa.ontrack.extension.git.resource;

import net.nemerosa.ontrack.extension.git.GitController;
import net.nemerosa.ontrack.extension.git.model.FormerGitConfiguration;
import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;

import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

public class GitConfigurationResourceDecorator extends AbstractResourceDecorator<FormerGitConfiguration> {

    private final SecurityService securityService;

    public GitConfigurationResourceDecorator(SecurityService securityService) {
        super(FormerGitConfiguration.class);
        this.securityService = securityService;
    }

    /**
     * Obfuscates the password
     */
    @Override
    public FormerGitConfiguration decorateBeforeSerialization(FormerGitConfiguration bean) {
        return bean.obfuscate();
    }

    @Override
    public List<Link> links(FormerGitConfiguration configuration, ResourceContext resourceContext) {
        boolean globalSettingsGranted = securityService.isGlobalFunctionGranted(GlobalSettings.class);
        return resourceContext.links()
                .self(on(GitController.class).getConfiguration(configuration.getName()))
                .link(Link.UPDATE, on(GitController.class).updateConfigurationForm(configuration.getName()))
                .link(Link.DELETE, on(GitController.class).deleteConfiguration(configuration.getName()))
                        // OK
                .build();
    }
}
