package net.nemerosa.ontrack.model.events;

/**
 * Service used to save events.
 */
public interface EventPostService {

    /**
     * Posts an event for the record.
     */
    void post(Event event);

}
