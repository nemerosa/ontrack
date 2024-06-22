package net.nemerosa.ontrack.extension.workflows.notifications

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.notifications.model.Notification
import net.nemerosa.ontrack.extension.notifications.queue.NotificationQueueItem
import net.nemerosa.ontrack.model.events.Event

interface WorkflowNotificationItemConverter {

    fun convertForQueue(event: Event, instanceId: String): NotificationQueueItem

    fun convertFromQueue(
        instanceId: String,
        channel: String,
        channelConfig: JsonNode,
        template: String?,
        queueItem: NotificationQueueItem,
    ): Notification

}