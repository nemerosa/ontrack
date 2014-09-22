package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.events.Event;

public interface EventRepository {

    void post(Event event);
    
}
