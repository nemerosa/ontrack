package net.nemerosa.ontrack.extension.workflows.notifications

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.NullNode
import net.nemerosa.ontrack.extension.notifications.model.Notification
import net.nemerosa.ontrack.extension.notifications.queue.NotificationQueueItem
import net.nemerosa.ontrack.extension.notifications.queue.NotificationQueueItemConverter
import net.nemerosa.ontrack.model.events.Event
import org.springframework.stereotype.Component

@Component
class WorkflowNotificationItemConverterImpl(
    private val notificationQueueItemConverter: NotificationQueueItemConverter,
) : WorkflowNotificationItemConverter {

    companion object {
        private const val CHANNEL = "workflow"
    }

    override fun convertForQueue(event: Event): NotificationQueueItem {
        return notificationQueueItemConverter.convertForQueue(
            Notification(
                channel = CHANNEL,
                channelConfig = NullNode.instance,
                event = event,
                template = null,
            )
        )
    }

    override fun convertFromQueue(
        channel: String,
        channelConfig: JsonNode,
        template: String?,
        queueItem: NotificationQueueItem,
    ): Notification {
        return notificationQueueItemConverter.convertFromQueue(queueItem).run {
            Notification(
                channel = channel,
                channelConfig = channelConfig,
                event = event,
                template = template,
            )
        }
    }
}