package net.nemerosa.ontrack.ui.resource;

import net.nemerosa.ontrack.model.structure.ProjectEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class ResourceDecorationContributorServiceImpl implements ResourceDecorationContributorService {

    private final ApplicationContext applicationContext;

    @Autowired
    public ResourceDecorationContributorServiceImpl(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void contribute(LinksBuilder linksBuilder, ProjectEntity projectEntity) {
        applicationContext.getBeansOfType(ResourceDecorationContributor.class).values().forEach(
                resourceDecorationContributor ->
                        resourceDecorationContributor.contribute(linksBuilder, projectEntity)
        );
    }
}
