package net.nemerosa.ontrack.extension.notifications.listener

import net.nemerosa.ontrack.extension.notifications.dispatching.NotificationDispatcher
import net.nemerosa.ontrack.extension.notifications.dispatching.NotificationDispatchingResult
import net.nemerosa.ontrack.extension.notifications.subscriptions.EventSubscriptionService
import net.nemerosa.ontrack.model.events.Event
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultEventListeningService(
    private val eventSubscriptionService: EventSubscriptionService,
    private val notificationDispatcher: NotificationDispatcher,
) : EventListeningService {

    override fun onEvent(event: Event) {
        // Gets the subscriptions matching this event
        var result = NotificationDispatchingResult.ZERO
        eventSubscriptionService.forEveryMatchingSubscription(event) { subscription ->
            // Sends the match to the dispatcher
            result += notificationDispatcher.dispatchEvent(event, subscription)
        }
        // TODO Sending metrics about the event dispatching
    }

}