package net.nemerosa.ontrack.service.events;

import net.nemerosa.ontrack.model.events.Event;
import net.nemerosa.ontrack.model.events.EventQueryService;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventQueryServiceImpl implements EventQueryService {

    @Override
    public List<Event> getEvents(ProjectEntityType entityType, ID entityId, int offset, int count) {
        // FIXME Method net.nemerosa.ontrack.service.events.EventQueryServiceImpl.getEvents
        return EventServiceImpl.events;
    }

}
