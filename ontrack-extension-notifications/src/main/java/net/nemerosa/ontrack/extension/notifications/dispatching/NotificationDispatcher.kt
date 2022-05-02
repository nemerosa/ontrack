package net.nemerosa.ontrack.extension.notifications.dispatching

import net.nemerosa.ontrack.extension.notifications.subscriptions.EventSubscription
import net.nemerosa.ontrack.model.events.Event

/**
 * For a given event and subscription, gets the appropriate channel, checks the configuration
 * and sends the notification to the notification queue.
 */
interface NotificationDispatcher {

    /**
     * Dispatches the event (if valid).
     *
     * @param event Event linked to the notification
     * @param eventSubscription Subscription to the event
     * @return Result of the dispatching
     */
    fun dispatchEvent(
        event: Event,
        eventSubscription: EventSubscription,
    ): NotificationDispatchingResult

}