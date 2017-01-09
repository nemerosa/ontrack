package net.nemerosa.ontrack.ui.resource;

import net.nemerosa.ontrack.model.structure.ProjectEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class ResourceDecorationContributorServiceImpl implements ResourceDecorationContributorService {

    private final ApplicationContext applicationContext;

    @Autowired
    public ResourceDecorationContributorServiceImpl(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public <T extends ProjectEntity> List<LinkDefinition<T>> getLinkDefinitions(Class<T> projectClass) {
        List<LinkDefinition<T>> definitions = new ArrayList<>();
        Collection<ResourceDecorationContributor> contributors = applicationContext.getBeansOfType(ResourceDecorationContributor.class).values();
        contributors.forEach(contributor -> {
            if (contributor.applyTo(projectClass)) {
                @SuppressWarnings("unchecked")
                ResourceDecorationContributor<T> tResourceDecorationContributor = (ResourceDecorationContributor<T>) contributor;
                definitions.addAll(tResourceDecorationContributor.getLinkDefinitions());
            }
        });
        return definitions;
    }
}
