package net.nemerosa.ontrack.extension.issues.combined;

import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;

import java.util.List;

public class CombinedIssueServiceConfigurationResourceDecorator extends AbstractResourceDecorator<CombinedIssueServiceConfiguration> {

    public CombinedIssueServiceConfigurationResourceDecorator() {
        super(CombinedIssueServiceConfiguration.class);
    }

    @Override
    public List<Link> links(CombinedIssueServiceConfiguration configuration, ResourceContext resourceContext) {
        return resourceContext.links()
                // TODO .self(on(CombinedIssueServiceController.class).getConfiguration(configuration.getName()))
                // TODO .link(Link.UPDATE, on(CombinedIssueServiceConfiguration.class).updateConfigurationForm(configuration.getName()))
                // TODO .link(Link.DELETE, on(CombinedIssueServiceConfiguration.class).deleteConfiguration(configuration.getName()))
                .build();
    }
}
