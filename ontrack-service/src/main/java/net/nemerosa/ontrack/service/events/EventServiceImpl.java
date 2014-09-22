package net.nemerosa.ontrack.service.events;

import net.nemerosa.ontrack.model.events.Event;
import net.nemerosa.ontrack.model.events.EventService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    // FIXME Uses a persistent repository
    public static final List<Event> events = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void post(Event event) {
        // FIXME Calls a repository
        events.add(event);
    }
}
