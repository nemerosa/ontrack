package net.nemerosa.ontrack.boot.graphql.schema;

import net.nemerosa.ontrack.model.events.EventQueryService;
import net.nemerosa.ontrack.model.events.EventType;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.Signature;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import net.nemerosa.ontrack.ui.resource.ResourceDecorator;

import java.util.List;
import java.util.Optional;

public abstract class AbstractGQLProjectEntityWithoutSignature<T extends ProjectEntity> extends AbstractGQLProjectEntity<T> {

    private final EventQueryService eventQueryService;

    public AbstractGQLProjectEntityWithoutSignature(URIBuilder uriBuilder, SecurityService securityService, Class<T> projectEntityClass, List<ResourceDecorator<?>> decorators, EventQueryService eventQueryService) {
        super(uriBuilder, securityService, projectEntityClass, decorators);
        this.eventQueryService = eventQueryService;
    }

    @Override
    protected Optional<Signature> getSignature(T entity) {
        return eventQueryService.getLastEventSignature(
                entity.getProjectEntityType(),
                entity.getId(),
                getEventCreationType()
        );
    }

    protected abstract EventType getEventCreationType();
}
