package net.nemerosa.ontrack.extension.github.resource;

import net.nemerosa.ontrack.extension.github.GitHubController;
import net.nemerosa.ontrack.extension.github.model.GitHubConfiguration;
import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;

import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

public class GitHubConfigurationResourceDecorator extends AbstractResourceDecorator<GitHubConfiguration> {

    private final SecurityService securityService;

    public GitHubConfigurationResourceDecorator(SecurityService securityService) {
        super(GitHubConfiguration.class);
        this.securityService = securityService;
    }

    /**
     * Obfuscates the password
     */
    @Override
    public GitHubConfiguration decorateBeforeSerialization(GitHubConfiguration bean) {
        return bean.obfuscate();
    }

    @Override
    public List<Link> links(GitHubConfiguration configuration, ResourceContext resourceContext) {
        boolean globalSettingsGranted = securityService.isGlobalFunctionGranted(GlobalSettings.class);
        return resourceContext.links()
                .self(on(GitHubController.class).getConfiguration(configuration.getName()))
                .link(Link.UPDATE, on(GitHubController.class).updateConfigurationForm(configuration.getName()))
                .link(Link.DELETE, on(GitHubController.class).deleteConfiguration(configuration.getName()))
                        // OK
                .build();
    }
}
