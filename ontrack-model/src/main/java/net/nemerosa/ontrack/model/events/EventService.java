package net.nemerosa.ontrack.model.events;

/**
 * Service used to save events.
 */
public interface EventService {

    /**
     * Posts an event for the record.
     */
    void post(Event event);

}
