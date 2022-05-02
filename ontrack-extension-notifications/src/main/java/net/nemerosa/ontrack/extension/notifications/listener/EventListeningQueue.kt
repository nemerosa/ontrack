package net.nemerosa.ontrack.extension.notifications.listener

import net.nemerosa.ontrack.model.events.Event

/**
 * Management of events before dispatching.
 */
interface EventListeningQueue {

    /**
     * Publishes an event for dispatching
     */
    fun publish(event: Event)

}