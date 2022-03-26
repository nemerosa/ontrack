package net.nemerosa.ontrack.extension.notifications.listener

import net.nemerosa.ontrack.extension.notifications.NotificationsConfigProperties
import net.nemerosa.ontrack.extension.notifications.dispatching.NotificationDispatcher
import net.nemerosa.ontrack.extension.notifications.dispatching.NotificationDispatchingResult
import net.nemerosa.ontrack.extension.notifications.subscriptions.EventSubscriptionService
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventListener
import org.springframework.stereotype.Component

/**
 * [Event listener][EventListener] which checks, for an event, if there are corresponding subscriptions,
 * and for each of the matches, send the event to the notification dispatcher.
 */
@Component
class EventSubscriptionsListener(
    private val eventSubscriptionService: EventSubscriptionService,
    private val notificationDispatcher: NotificationDispatcher,
    private val notificationsConfigProperties: NotificationsConfigProperties,
) : EventListener {

    override fun onEvent(event: Event) {
        // Checks if notifications are enabled
        if (!notificationsConfigProperties.enabled) return
        // Gets the subscriptions matching this event
        var result = NotificationDispatchingResult.ZERO
        eventSubscriptionService.forEveryMatchingSubscription(event) { subscription ->
            // Sends the match to the dispatcher
            result += notificationDispatcher.dispatchEvent(event, subscription)
        }
        // TODO Sending metrics about the event dispatching
    }

}