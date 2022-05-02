package net.nemerosa.ontrack.extension.notifications.listener

import io.micrometer.core.instrument.MeterRegistry
import net.nemerosa.ontrack.extension.notifications.NotificationsConfigProperties
import net.nemerosa.ontrack.extension.notifications.metrics.NotificationsMetrics
import net.nemerosa.ontrack.extension.notifications.metrics.incrementForEvent
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
    private val meterRegistry: MeterRegistry,
) : EventListener {

    override fun onEvent(event: Event) {
        // Checks if notifications are enabled
        if (!notificationsConfigProperties.enabled) return
        // Counting the number of event received for dispatching
        meterRegistry.incrementForEvent(
            NotificationsMetrics.event_listening_received,
            event
        )
        // Publishes the event on the queue
        eventListeningQueue.publish(event)
    }

}