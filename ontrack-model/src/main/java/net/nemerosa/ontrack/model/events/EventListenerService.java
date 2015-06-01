package net.nemerosa.ontrack.model.events;

/**
 * Management of {@link EventListener} instances.
 */
public interface EventListenerService {

    /**
     * Receives a notification after an event has been created.
     *
     * @param event Event which has been created.
     */
    void onEvent(Event event);

}
