package net.nemerosa.ontrack.extension.gitlab.resource;

import net.nemerosa.ontrack.extension.gitlab.GitLabController;
import net.nemerosa.ontrack.extension.gitlab.model.GitLabConfiguration;
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
public class GitLabConfigurationResourceDecorator extends AbstractResourceDecorator<GitLabConfiguration> {

    private final SecurityService securityService;

    @Autowired
    public GitLabConfigurationResourceDecorator(SecurityService securityService) {
        super(GitLabConfiguration.class);
        this.securityService = securityService;
    }

    /**
     * Obfuscates the password
     */
    @Override
    public GitLabConfiguration decorateBeforeSerialization(GitLabConfiguration bean) {
        return bean.obfuscate();
    }

    @Override
    public List<Link> links(GitLabConfiguration configuration, ResourceContext resourceContext) {
        boolean globalSettingsGranted = securityService.isGlobalFunctionGranted(GlobalSettings.class);
        return resourceContext.links()
                .self(on(GitLabController.class).getConfiguration(configuration.getName()))
                .link(Link.UPDATE, on(GitLabController.class).updateConfigurationForm(configuration.getName()), globalSettingsGranted)
                .link(Link.DELETE, on(GitLabController.class).deleteConfiguration(configuration.getName()), globalSettingsGranted)
                // OK
                .build();
    }
}
