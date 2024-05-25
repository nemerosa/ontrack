package net.nemerosa.ontrack.model.events

/**
 * Defines a service which listens to events.
 */
interface EventListener {
    /**
     * Receives a notification after an event has been created.
     *
     * @param event Event which has been created.
     */
    fun onEvent(event: Event)
}
