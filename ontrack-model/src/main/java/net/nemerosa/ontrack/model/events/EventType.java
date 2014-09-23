package net.nemerosa.ontrack.model.events;

/**
 * Type for an event.
 */
public interface EventType {

    /**
     * Identifier for this type
     */
    String getId();

    /**
     * Template to be used to render the event message
     */
    String getTemplate();

}
