package net.nemerosa.ontrack.ui.resource;

import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * NOP contributor to make injection of {@link ResourceDecorationContributorServiceImpl} work.
 *
 * @see ResourceDecorationContributorServiceImpl
 */
@Component
public class NOPResourceDecorationContributor implements ResourceDecorationContributor {

    @Override
    public List<LinkDefinition> getLinkDefinitions() {
        return Collections.emptyList();
    }

    @Override
    public boolean applyTo(ProjectEntityType projectEntityType) {
        return false;
    }

}
