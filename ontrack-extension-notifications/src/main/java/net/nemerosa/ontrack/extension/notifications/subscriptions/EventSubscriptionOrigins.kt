package net.nemerosa.ontrack.extension.notifications.subscriptions

/**
 * Non exhaustive set of origins
 */
object EventSubscriptionOrigins {

    /**
     * Unknown origin (for backward compatibility)
     */
    const val UNKNOWN = "unknown"

    /**
     * Subscription added though CasC.
     */
    const val CASC = "casc"

    /**
     * Subscription added though the API.
     */
    const val API = "api"

}