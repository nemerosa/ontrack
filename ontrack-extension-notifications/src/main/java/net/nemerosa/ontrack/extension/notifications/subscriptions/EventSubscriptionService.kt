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
    fun subscribe(subscription: EventSubscription)

    /**
     * Looks for a subscription using its name
     *
     * @param projectEntity Entity to look for
     * @param name Name of the subscription
     * @return Subscription or null if not found
     */
    fun findSubscriptionByName(projectEntity: ProjectEntity?, name: String): EventSubscription?

    /**
     * Deleting a subscription using its name
     *
     * @param projectEntity Entity to look for
     * @param name Name of the subscription
     */
    fun deleteSubscriptionByName(projectEntity: ProjectEntity?, name: String)

    /**
     * Deletes all subscriptions for a given entity
     *
     * @param projectEntity Entity to delete the subscriptions for
     */
    fun deleteSubscriptionsByEntity(projectEntity: ProjectEntity)

    /**
     * Deletes all subscriptions for a given entity and origin
     *
     * @param projectEntity Entity to delete the subscriptions for
     * @param origin Origin to delete the subscriptions for
     */
    fun deleteSubscriptionsByEntityAndOrigin(projectEntity: ProjectEntity, origin: String)

    /**
     * Disables a subscription using its name
     *
     * @param projectEntity Entity to look for
     * @param name Name of the subscription
     */
    fun disableSubscriptionByName(projectEntity: ProjectEntity?, name: String)

    /**
     * Enables a subscription using its name
     *
     * @param projectEntity Entity to look for
     * @param name ID of the subscription
     */
    fun enableSubscriptionByName(projectEntity: ProjectEntity?, name: String)

    /**
     * Getting a paginated list of subscriptions which match against the given [filter].
     *
     * Subscriptions are ordered in the following way:
     *
     * * global filters first, entity-scoped subscriptions next
     * * creation date (most the most recent to the oldest)
     *
     * @param filter Subscription filter
     * @return Paginated list of subscriptions
     */
    fun filterSubscriptions(filter: EventSubscriptionFilter): PaginatedList<EventSubscription>

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

    /**
     * Renames a subscription.
     *
     * @param projectEntity Entity holding the subscription, `null` for a global subscription
     * @param name Name of the subscription
     * @param newName Name to set
     * @return Renamed subscription (`null` if not found)
     */
    fun renameSubscription(projectEntity: ProjectEntity?, name: String, newName: String): EventSubscription?

}