package net.nemerosa.ontrack.extension.notifications.queue

import net.nemerosa.ontrack.extension.notifications.model.Notification

interface NotificationQueue {

    fun publish(item: Notification): Boolean

}