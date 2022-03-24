package net.nemerosa.ontrack.extension.notifications.queue

import net.nemerosa.ontrack.extension.notifications.model.Notification

interface NotificationQueueItemConverter {
    fun convertForQueue(item: Notification): NotificationQueueItem
    fun convertFromQueue(item: NotificationQueueItem): Notification
}