package net.nemerosa.ontrack.model.events

/**
 * Type for an event.
 */
interface EventType {
    /**
     * Identifier for this type
     */
    val id: String

    /**
     * Template to be used to render the event message
     */
    val template: String

    /**
     * Description for this event
     */
    val description: String
}
