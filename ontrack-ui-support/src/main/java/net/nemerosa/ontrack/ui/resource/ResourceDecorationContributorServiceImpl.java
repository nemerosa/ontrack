package net.nemerosa.ontrack.ui.resource;

import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class ResourceDecorationContributorServiceImpl implements ResourceDecorationContributorService {

    private final Collection<ResourceDecorationContributor> contributors;

    @Autowired
    public ResourceDecorationContributorServiceImpl(Collection<ResourceDecorationContributor> contributors) {
        this.contributors = contributors;
    }

    @Override
    public <T extends ProjectEntity> List<LinkDefinition<T>> getLinkDefinitions(ProjectEntityType projectEntityType) {
        List<LinkDefinition<T>> definitions = new ArrayList<>();
        contributors.forEach(contributor -> {
            if (contributor.applyTo(projectEntityType)) {
                @SuppressWarnings("unchecked")
                ResourceDecorationContributor<T> tResourceDecorationContributor = (ResourceDecorationContributor<T>) contributor;
                definitions.addAll(tResourceDecorationContributor.getLinkDefinitions());
            }
        });
        return definitions;
    }
}
