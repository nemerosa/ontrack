package net.nemerosa.ontrack.extension.notifications.queue

import net.nemerosa.ontrack.extension.notifications.model.Notification
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

@Component
class DefaultNotificationQueueItemConverter(
    private val eventFactory: EventFactory,
    private val structureService: StructureService,
) : NotificationQueueItemConverter {

    override fun convertForQueue(item: Notification) = NotificationQueueItem(
        channel = item.channel,
        channelConfig = item.channelConfig,
        eventType = item.event.eventType.id,
        signature = item.event.signature,
        entities = item.event.entities.mapValues { (_, entity) -> entity.id() },
        ref = item.event.ref,
        values = item.event.values,
    )

    override fun convertFromQueue(item: NotificationQueueItem) = Notification(
        channel = item.channel,
        channelConfig = item.channelConfig,
        event = Event(
            eventFactory.toEventType(item.eventType),
            item.signature,
            item.entities.mapValues { (type, id) ->
                type.getEntityFn(structureService).apply(ID.of(id))
            },
            item.ref,
            item.values,
        )
    )
}