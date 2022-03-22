package net.nemerosa.ontrack.extension.notifications.channels

/**
 * Getting access to the list of notification channels.
 */
interface NotificationChannelRegistry {

    fun findChannel(type: String): NotificationChannel<*>?

}