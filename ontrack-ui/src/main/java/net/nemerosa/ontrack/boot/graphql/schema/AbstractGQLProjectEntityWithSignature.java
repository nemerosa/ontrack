package net.nemerosa.ontrack.boot.graphql.schema;

import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.PropertyService;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import net.nemerosa.ontrack.ui.resource.ResourceDecorator;

import java.util.List;

public abstract class AbstractGQLProjectEntityWithSignature<T extends ProjectEntity> extends AbstractGQLProjectEntity<T> {

    public AbstractGQLProjectEntityWithSignature(
            URIBuilder uriBuilder,
            SecurityService securityService,
            Class<T> projectEntityClass,
            List<ResourceDecorator<?>> decorators,
            PropertyService propertyService
    ) {
        super(uriBuilder, securityService, projectEntityClass, decorators, propertyService);
    }
}
