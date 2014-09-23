package net.nemerosa.ontrack.service.events;

import net.nemerosa.ontrack.model.events.Event;
import net.nemerosa.ontrack.model.events.EventFactory;
import net.nemerosa.ontrack.model.events.EventQueryService;
import net.nemerosa.ontrack.model.structure.Entity;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.model.structure.StructureService;
import net.nemerosa.ontrack.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
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
        List<Integer> projectIds = structureService.getProjectList().stream().map(Entity::id).collect(Collectors.toList());
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
        List<Integer> projectIds = structureService.getProjectList().stream().map(Entity::id).collect(Collectors.toList());
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

}
