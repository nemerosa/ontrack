package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.model.events.Event

/**
 * Management of the subscriptions.
 */
interface EventSubscriptionService {

    /**
     * Looping over all matching subscriptions for a given event.
     *
     * @param event Event to match
     * @param code Code to run against the subscription
     */
    fun forEveryMatchingSubscription(event: Event, code: (subscription: EventSubscription) -> Unit)

}