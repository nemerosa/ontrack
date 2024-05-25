package net.nemerosa.ontrack.model.events;

/**
 * Defines a service which listens to events.
 */
public interface EventListener {

    /**
     * Receives a notification after an event has been created.
     *
     * @param event Event which has been created.
     */
    void onEvent(Event event);
}
