package net.nemerosa.ontrack.service.events;

import net.nemerosa.ontrack.model.events.Event;
import net.nemerosa.ontrack.model.events.EventFactory;
import net.nemerosa.ontrack.model.events.EventQueryService;
import net.nemerosa.ontrack.model.events.EventType;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class EventQueryServiceImpl implements EventQueryService {

    private final StructureService structureService;
    private final EventFactory eventFactory;
    private final EventRepository eventRepository;

    @Autowired
    public EventQueryServiceImpl(StructureService structureService, EventFactory eventFactory, EventRepository eventRepository) {
        this.structureService = structureService;
        this.eventFactory = eventFactory;
        this.eventRepository = eventRepository;
    }

    @Override
    public List<Event> getEvents(int offset, int count) {
        // Gets the list of projects the current user is allowed to view
        List<Integer> projectIds = getAllowedProjectIds();
        // Performs the query
        return eventRepository.query(
                projectIds,
                offset,
                count,
                (type, id) -> type.getEntityFn(structureService).apply(id),
                eventFactory::toEventType
        );
    }

    @Override
    public List<Event> getEvents(ProjectEntityType entityType, ID entityId, int offset, int count) {
        // Gets the list of projects the current user is allowed to view
        List<Integer> projectIds = getAllowedProjectIds();
        // Performs the query
        return eventRepository.query(
                projectIds,
                entityType,
                entityId,
                offset,
                count,
                (type, id) -> type.getEntityFn(structureService).apply(id),
                eventFactory::toEventType
        );
    }

    private List<Integer> getAllowedProjectIds() {
        return structureService.getProjectList().stream().map(Entity::id).collect(Collectors.toList());
    }

    @Override
    public List<Event> getEvents(ProjectEntityType entityType, ID entityId, EventType eventType, int offset, int count) {
        return eventRepository.query(
                getAllowedProjectIds(),
                eventType,
                entityType,
                entityId,
                offset,
                count,
                (type, id) -> type.getEntityFn(structureService).apply(id),
                eventFactory::toEventType
        );
    }

    @Override
    public Optional<Signature> getLastEventSignature(ProjectEntityType entityType, ID entityId, EventType eventType) {
        return eventRepository.getLastEventSignature(entityType, entityId, eventType);
    }

}
