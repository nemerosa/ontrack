package net.nemerosa.ontrack.extension.github.resource;

import net.nemerosa.ontrack.extension.github.GitHubController;
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration;
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
public class GitHubEngineConfigurationResourceDecorator extends AbstractResourceDecorator<GitHubEngineConfiguration> {

    private final SecurityService securityService;

    @Autowired
    public GitHubEngineConfigurationResourceDecorator(SecurityService securityService) {
        super(GitHubEngineConfiguration.class);
        this.securityService = securityService;
    }

    /**
     * Obfuscates the password
     */
    @Override
    public GitHubEngineConfiguration decorateBeforeSerialization(GitHubEngineConfiguration bean) {
        return bean.obfuscate();
    }

    @Override
    public List<Link> links(GitHubEngineConfiguration configuration, ResourceContext resourceContext) {
        boolean globalSettingsGranted = securityService.isGlobalFunctionGranted(GlobalSettings.class);
        return resourceContext.links()
                .self(on(GitHubController.class).getConfiguration(configuration.getName()))
                .link(Link.UPDATE, on(GitHubController.class).updateConfigurationForm(configuration.getName()), globalSettingsGranted)
                .link(Link.DELETE, on(GitHubController.class).deleteConfiguration(configuration.getName()), globalSettingsGranted)
                        // OK
                .build();
    }
}
