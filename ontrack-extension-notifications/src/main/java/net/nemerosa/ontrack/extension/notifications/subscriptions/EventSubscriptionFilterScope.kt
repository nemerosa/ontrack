package net.nemerosa.ontrack.extension.notifications.subscriptions

/**
 * Scope when looking for subscriptions.
 */
enum class EventSubscriptionFilterScope {

    /**
     * Global level only
     */
    GLOBAL,

    /**
     * Entity level only
     */
    ENTITY,

    /**
     * Mix of the two
     */
    MIXED

}