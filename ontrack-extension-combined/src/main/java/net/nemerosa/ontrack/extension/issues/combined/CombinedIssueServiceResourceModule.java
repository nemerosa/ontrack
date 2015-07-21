package net.nemerosa.ontrack.extension.issues.combined;

import net.nemerosa.ontrack.ui.resource.AbstractResourceModule;
import net.nemerosa.ontrack.ui.resource.ResourceDecorator;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

/**
 * Declaration of resource bindings for the combined issue service extension.
 */
@Component
public class CombinedIssueServiceResourceModule extends AbstractResourceModule {

    @Override
    public Collection<ResourceDecorator<?>> decorators() {
        return Collections.singletonList(
                new CombinedIssueServiceConfigurationResourceDecorator()
        );
    }

}
