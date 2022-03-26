package net.nemerosa.ontrack.extension.notifications.listener

import net.nemerosa.ontrack.extension.notifications.NotificationsConfigProperties
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventListener
import org.springframework.stereotype.Component

/**
 * [Event listener][EventListener] which checks, for an event, if there are corresponding subscriptions,
 * and for each of the matches, send the event to the notification dispatcher.
 */
@Component
class EventSubscriptionsListener(
    private val eventListeningQueue: EventListeningQueue,
    private val notificationsConfigProperties: NotificationsConfigProperties,
) : EventListener {

    override fun onEvent(event: Event) {
        // Checks if notifications are enabled
        if (!notificationsConfigProperties.enabled) return
        // Publishes the event on the queue
        eventListeningQueue.publish(event)
    }

}