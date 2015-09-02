package net.nemerosa.ontrack.extension.issues.combined;

import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@Component
public class CombinedIssueServiceConfigurationResourceDecorator extends AbstractResourceDecorator<CombinedIssueServiceConfiguration> {

    public CombinedIssueServiceConfigurationResourceDecorator() {
        super(CombinedIssueServiceConfiguration.class);
    }

    @Override
    public List<Link> links(CombinedIssueServiceConfiguration configuration, ResourceContext resourceContext) {
        return resourceContext.links()
                .self(on(CombinedIssueServiceController.class).getConfiguration(configuration.getName()))
                .link(Link.UPDATE, on(CombinedIssueServiceController.class).updateConfigurationForm(configuration.getName()), GlobalSettings.class)
                .link(Link.DELETE, on(CombinedIssueServiceController.class).deleteConfiguration(configuration.getName()), GlobalSettings.class)
                .build();
    }
}
