package net.nemerosa.ontrack.model.events

/**
 * Service used to save events.
 */
interface EventPostService {
    /**
     * Posts an event for the record.
     */
    fun post(event: Event)
}
