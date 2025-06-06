package net.nemerosa.ontrack.ui.resource;

import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class ResourceDecorationContributorServiceImpl implements ResourceDecorationContributorService {

    private final Logger logger = LoggerFactory.getLogger(ResourceDecorationContributorServiceImpl.class);
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
                try {
                    @SuppressWarnings("unchecked")
                    ResourceDecorationContributor<T> tResourceDecorationContributor = (ResourceDecorationContributor<T>) contributor;
                    definitions.addAll(tResourceDecorationContributor.getLinkDefinitions());
                } catch (Exception ex) {
                    // Logging
                    logger.error(
                            String.format(
                                    "Issue when collecting UI resource decoration: type: %s, decorator: %s",
                                    projectEntityType.name(),
                                    contributor.getClass().getName()
                            ),
                            ex);
                }
            }
        });
        return definitions;
    }
}
