package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.structure.ProjectEntity

/**
 * Management of the subscriptions.
 */
interface EventSubscriptionService {

    /**
     * Registering a subscription
     */
    fun subscribe(subscription: EventSubscription): SavedEventSubscription

    /**
     * Looks for a subscription using its ID
     *
     * @param projectEntity Entity to look for
     * @param id ID of the subscription
     * @return Subscription or null if not found
     */
    fun findSubscriptionById(projectEntity: ProjectEntity?, id: String): EventSubscription?

    /**
     * Looping over all matching subscriptions for a given event.
     *
     * @param event Event to match
     * @param code Code to run against the subscription
     */
    fun forEveryMatchingSubscription(event: Event, code: (subscription: EventSubscription) -> Unit)

}