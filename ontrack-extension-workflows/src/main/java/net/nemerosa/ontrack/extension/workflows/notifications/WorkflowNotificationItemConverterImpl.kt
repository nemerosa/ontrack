package net.nemerosa.ontrack.extension.workflows.notifications

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.NullNode
import net.nemerosa.ontrack.extension.notifications.model.Notification
import net.nemerosa.ontrack.extension.notifications.model.createData
import net.nemerosa.ontrack.extension.notifications.queue.NotificationQueueItem
import net.nemerosa.ontrack.extension.notifications.queue.NotificationQueueItemConverter
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.SerializableEvent
import net.nemerosa.ontrack.model.events.SerializableEventService
import org.springframework.stereotype.Component

@Component
class WorkflowNotificationItemConverterImpl(
    private val notificationQueueItemConverter: NotificationQueueItemConverter,
    private val workflowNotificationSource: WorkflowNotificationSource,
    private val serializableEventService: SerializableEventService,
) : WorkflowNotificationItemConverter {

    companion object {
        private const val CHANNEL = "workflow"
    }

    override fun convertForQueue(event: Event, instanceId: String): NotificationQueueItem {
        return notificationQueueItemConverter.convertForQueue(
            Notification(
                source = workflowNotificationSource.createData(
                    WorkflowNotificationSourceDataType(
                        workflowInstanceId = instanceId
                    )
                ),
                channel = CHANNEL,
                channelConfig = NullNode.instance,
                event = event,
                template = null,
            )
        )
    }

    override fun convertFromQueue(
        instanceId: String,
        channel: String,
        channelConfig: JsonNode,
        template: String?,
        event: SerializableEvent,
    ): Notification {
        return Notification(
            source = workflowNotificationSource.createData(
                WorkflowNotificationSourceDataType(
                    workflowInstanceId = instanceId
                )
            ),
            channel = channel,
            channelConfig = channelConfig,
            event = serializableEventService.hydrate(event),
            template = template,
        )
    }
}