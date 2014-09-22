package net.nemerosa.ontrack.service.events;

import net.nemerosa.ontrack.model.events.Event;
import net.nemerosa.ontrack.model.events.EventQueryService;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventQueryServiceImpl implements EventQueryService {

    private final EventRepository eventRepository;

    @Autowired
    public EventQueryServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public List<Event> getEvents(ProjectEntityType entityType, ID entityId, int offset, int count) {
        // FIXME Method net.nemerosa.ontrack.service.events.EventQueryServiceImpl.getEvents
        return null;
    }

}
