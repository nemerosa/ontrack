package net.nemerosa.ontrack.extension.notifications.processing

import net.nemerosa.ontrack.extension.notifications.queue.NotificationQueueItem

interface NotificationProcessingService {

    fun process(item: NotificationQueueItem)

}