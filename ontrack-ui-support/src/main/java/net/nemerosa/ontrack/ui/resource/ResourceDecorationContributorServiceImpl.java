package net.nemerosa.ontrack.ui.resource;

import net.nemerosa.ontrack.model.structure.NameDescription;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.model.support.ApplicationLogEntry;
import net.nemerosa.ontrack.model.support.ApplicationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class ResourceDecorationContributorServiceImpl implements ResourceDecorationContributorService {

    private final ApplicationLogService logService;
    private final Collection<ResourceDecorationContributor> contributors;

    @Autowired
    public ResourceDecorationContributorServiceImpl(ApplicationLogService logService, Collection<ResourceDecorationContributor> contributors) {
        this.logService = logService;
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
                    logService.log(
                            ApplicationLogEntry.fatal(
                                    ex,
                                    NameDescription.nd(
                                            "ui-resource-decoration",
                                            "Issue when collecting UI resource decoration"
                                    ),
                                    contributor.getClass().getName()
                            )
                                    .withDetail("ui-resource-type", projectEntityType.name())
                                    .withDetail("ui-resource-decorator", contributor.getClass().getName())
                    );
                }
            }
        });
        return definitions;
    }
}
