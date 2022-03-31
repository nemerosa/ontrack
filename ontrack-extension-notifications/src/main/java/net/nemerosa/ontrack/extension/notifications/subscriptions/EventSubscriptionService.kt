package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.pagination.PaginatedList
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
     * Deleting a subscription using its ID
     *
     * @param projectEntity Entity to look for
     * @param id ID of the subscription
     */
    fun deleteSubscriptionById(projectEntity: ProjectEntity?, id: String)

    /**
     * Disables a subscription using its ID
     *
     * @param projectEntity Entity to look for
     * @param id ID of the subscription
     */
    fun disableSubscriptionById(projectEntity: ProjectEntity?, id: String): SavedEventSubscription

    /**
     * Enables a subscription using its ID
     *
     * @param projectEntity Entity to look for
     * @param id ID of the subscription
     */
    fun enableSubscriptionById(projectEntity: ProjectEntity?, id: String): SavedEventSubscription

    /**
     * Getting a paginated list of subscriptions which match against the given [filter].
     *
     * Subscriptions are ordered in the following way:
     *
     * * global filters first, entity-scoped subscriptions next
     * * creation date (most the most recent to the oldest)
     *
     * @param filter Subscritption filter
     * @return Paginated list of subscriptions
     */
    fun filterSubscriptions(filter: EventSubscriptionFilter): PaginatedList<SavedEventSubscription>

    /**
     * Looping over all matching subscriptions for a given event.
     *
     * @param event Event to match
     * @param code Code to run against the subscription
     */
    fun forEveryMatchingSubscription(event: Event, code: (subscription: EventSubscription) -> Unit)

    /**
     * Removes all global subscriptions
     */
    fun removeAllGlobal()

}