package net.nemerosa.ontrack.extension.notifications.queue

interface NotificationQueue {

    fun publish(item: NotificationQueueItem): Boolean

}